/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  package/Class: Clusters
 *      Tracks block positions for multiblock shenanigans.
 *  Copyright (c) 2020 D0CTOR ZED
 *  This code is licensed under the MIT License, available in the root folder.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package zed.d0c.floormats.clusters;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import zed.d0c.floormats.punchcards.PunchCards;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static net.minecraft.block.SixWayBlock.FACING_TO_PROPERTY_MAP;

/*  **************************************************************************
 *
 *  Clusters - Multiblock of identical blocks
 *
 *  Stores cluster data to reduce the need to repeatedly reiterate in-world
 *  blocks during structure changes.
 *
 *  DATA STRUCTURES:
 *  A set of cluster nodes.  Each node is a set of linked blocks.
 *
 *  NODE STRUCTURE:
 *  Each node is a collection of connected blocks.  It is a list of blocks in
 *  the cluster and an indicator of which blocks are directly powered.
 *
 *  MAYBE DOs:
 *
 *  Add some checks to avoid searching large nodes.  Block type check
 *  and coordinate range check would be appropriate.  If a node had a pair
 *  of positions representing their bounding box, a quick check would show
 *  if a pos could possibly be in bounds.  Caveat: This would also add more
 *  processing during a check while it check bounds, so might save time for
 *  lots of large structures, but might waste time for lots of small structures.
 *  This would need some actual timed test to see which is better for server
 *  performance.  Currently not expecting scales large enough to matter.
 *
 *  Replace hard coded FloorMat specific bits with a more abstract approach.
 *  The specific bits should be passed to the constructor and stored here.
 *  Not really needed until this package finds a second use.
 *
 *****************************************************************************/

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Clusters {

    public Clusters() { super(); }

    public static class ClustersSet extends HashSet<ClustersNode> {
    }

    // clustersRegistry is a map of dimensions to sets of ClustersNode.
    public static class ClustersRegistry extends HashMap<RegistryKey<World>, ClustersSet> {
        private ClustersSet getClusterSet(RegistryKey<World> key) {
            if ( !containsKey(key) ) {
                put(key,new ClustersSet());
            }
            return get(key);
        }
        private ClustersSet getClusterSet(World worldIn) {
            RegistryKey<World> key = worldIn.getDimensionKey();
            return getClusterSet(key);
        }
    }

    private static final ClustersRegistry CLUSTERS_REGISTRY = new ClustersRegistry();

    public static ClustersRegistry getClustersRegistry() {
        return CLUSTERS_REGISTRY;
    }

    private void addNode(World worldIn, ClustersNode newNode) {
        synchronized (Clusters.getClustersRegistry()) {
            CLUSTERS_REGISTRY.getClusterSet(worldIn).add(newNode);
        }
        PunchCards.setDirty();
    }

    private void removeNode(World worldIn, ClustersNode exNode) {
        synchronized (Clusters.getClustersRegistry()) {
            CLUSTERS_REGISTRY.getClusterSet(worldIn).remove(exNode);
        }
        PunchCards.setDirty();
    }

    private void addNodeSet(World worldIn, ClustersSet newNodeSet) {
        synchronized (Clusters.getClustersRegistry()) {
            CLUSTERS_REGISTRY.getClusterSet(worldIn).addAll(newNodeSet);
        }
        PunchCards.setDirty();
    }

    // getNode will either find an existing node or create a new one.
    private ClustersNode getNode(World worldIn, BlockState state, BlockPos pos) {
        Block block = state.getBlock();
        for (ClustersNode node : CLUSTERS_REGISTRY.getClusterSet(worldIn)) {
            if (node.contains(block, pos)) {
                return node;
            }
        }
        ClustersNode newNode = new ClustersNode(state.getBlock(),pos);
        addNode(worldIn,newNode);
        return newNode;
    }

    public boolean hasNode(World worldIn, BlockState state, BlockPos pos) {
        Block block = state.getBlock();
        for (ClustersNode node : CLUSTERS_REGISTRY.getClusterSet(worldIn)) {
            if (node.contains(block, pos)) {
                return true;
            }
        }
        return false;
    }

    // needs to return true if iPos wasn't already marked as directly powered (regardless of whether mat was previously powered
    // this will determine if the block needs to tick.  It will tick until not directly powered.
    public boolean applyDirectPower(World worldIn, BlockPos iPos, @Nullable ArrayList<PlayerEntity> playerList) {
        ClustersNode node = getNode(worldIn,worldIn.getBlockState(iPos),iPos);
        boolean directPowerApplied = node.powerNode(worldIn,iPos, playerList);
        PunchCards.setDirty();
        return directPowerApplied;
    }

    public boolean removeDirectPower(BlockState state, World worldIn, BlockPos pos) {
        boolean blockDepowered = getNode(worldIn,state,pos).depowerBlock(worldIn,pos);
        PunchCards.setDirty();
        return blockDepowered;
    }

    // be mindful that addToClusters can be called on blocks already added to their cluster.  Don't assume it is new.
    public void addToClusters(World worldIn, BlockPos pos, BlockState state) {
        final BlockPos iPos = pos.toImmutable();
        final Block block = state.getBlock();
        ClustersNode node = getNode(worldIn,state,iPos);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (state.get(FACING_TO_PROPERTY_MAP.get(direction))) {
                BlockPos neighborPos = iPos.offset(direction);
                // the check for same block type; could at some point be it's own method to allow multi-type clusters
                // if found, may need to merge the nodes
                if (worldIn.getBlockState(neighborPos).getBlock() == block) {
                    ClustersNode adjacentNode = getNode(worldIn, state, neighborPos);
                    // It may have already merged with the node if the adjacent node touches multiple sides.
                    if (!node.equals(adjacentNode)) {
                        // To be safe, I try to sync any time I would like a series of changes to be atomic.
                        synchronized (Clusters.getClustersRegistry()) {
                            node.absorbOtherNode(adjacentNode);
                            removeNode(worldIn, adjacentNode);
                        }
                    }
                }
            }
        }
        // new block may have been added and existing nodes may have merged.
        // some of the blocks may therefore not be powered, even though the node is considered powered
        // they need to power, not get marked as directly powered, and update their neighbors as appropriate.
        node.powerAsNeeded(worldIn);

        PunchCards.setDirty();
    }

    public void removeFromClusters(World worldIn, BlockPos iPos, BlockState state) {
        // final Block block = state.getBlock();
        final boolean wasPowered = state.get(BlockStateProperties.POWERED);
        final ClustersNode thisNode = getNode(worldIn,state,iPos);

        int neighborCount = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            // BlockPos neighborPos = iPos.offset(direction);
            // the check for same block type should at some point be it's own method to allow multi-type clusters
            // if ( worldIn.getBlockState(neighborPos).getBlock() == block ) {
            //     ++neighborCount;
            // }
            if (state.get(FACING_TO_PROPERTY_MAP.get(direction))) {
                ++neighborCount;

            }
        }

        synchronized (Clusters.getClustersRegistry()) {
            switch (neighborCount) {
                case 0:    // without neighbors, this removes the node.
                    thisNode.removeLink(worldIn,wasPowered);
                    removeNode(worldIn, thisNode);
                    break;
                case 1:    // there is one neighbor... the node survives minus one block.
                    thisNode.removePos(worldIn, iPos);
                    if (wasPowered && !thisNode.isPowered()) {
                        thisNode.depowerNode(worldIn);
                    }
                    break;
                default:    // more than one neighbor.  The cluster may split into multiple parts
                            // if node was powered, each part needs to reevaluate power.
                    thisNode.removePos(worldIn, iPos);
                    ClustersSet setOfNodes = thisNode.reformNode(worldIn);
                    removeNode(worldIn, thisNode);
                    addNodeSet(worldIn, setOfNodes);
                    if (wasPowered) {
                        for (ClustersNode nodeForPowerCheck : setOfNodes) {
                            if (!nodeForPowerCheck.isPowered()) {
                                nodeForPowerCheck.depowerNode(worldIn);
                            }
                        }
                    }
            }
        }
        PunchCards.setDirty();
    }

    public void alterClusters(World worldIn, BlockPos iPos, BlockState stateIn) {
        ClustersNode node = getNode(worldIn,stateIn,iPos);
        boolean wasPowered = node.isPowered();
        synchronized (Clusters.getClustersRegistry()) {
            ClustersSet setOfNodes = node.reformNode(worldIn);
            removeNode(worldIn, node);
            addNodeSet(worldIn, setOfNodes);
            if (wasPowered) { // doesn't need sync, but does need setOfNodes
                for (ClustersNode nodeForPowerCheck : setOfNodes) {
                    if (!nodeForPowerCheck.isPowered()) {
                        nodeForPowerCheck.depowerNode(worldIn);
                    }
                }
            }
        }
        PunchCards.setDirty();
    }

    // Used by gold floormats to make them only function for the player(s) that placed the blocks
    // Considering plans to implement ways of doing this for other floormats as well.
    public void registerUniqueID(World worldIn, BlockPos pos, BlockState state, UUID uniqueID) {
        getNode(worldIn, state, pos).addUniqueID(uniqueID);
    }

    public boolean canAlter(World worldIn, BlockPos pos, BlockState state, UUID uniqueID) {
        return getNode(worldIn, state, pos).canAccess(uniqueID);
    }

    public static int cmdReset(ServerWorld serverWorld) {
        int correctionCount = 0;
        synchronized (Clusters.getClustersRegistry()) {
            for (RegistryKey<World> dim : CLUSTERS_REGISTRY.keySet()) {
                ClustersSet emptyNodes = new ClustersSet();
                for (ClustersNode node : CLUSTERS_REGISTRY.get(dim)) {
                    correctionCount += node.cmdResetNode(serverWorld);
                    if (node.isEmpty()) {
                        emptyNodes.add(node);
                    }
                }
                CLUSTERS_REGISTRY.get(dim).removeAll(emptyNodes);
            }
        }
        PunchCards.setDirty();
        return correctionCount;
    }

    public boolean linkClusters(World worldIn, BlockPos iPos, BlockState state, BlockPos linkPos, BlockState linkState) {
        ClustersNode node = getNode(worldIn,state,iPos);
        ClustersNode linkNode = getNode(worldIn,linkState,linkPos);
        if (node.equals(linkNode)) {
            node.removeLink(worldIn,node.isPowered());
            PunchCards.setDirty();
            return false;
        }
        boolean powered = node.isPowered();
        boolean linkPowered = linkNode.isPowered();
        if (powered && !linkPowered) {
            linkNode.powerNode(worldIn, null, null);
        } else if (linkPowered && !powered) {
            node.powerNode(worldIn, null, null);
        }
        node.linkTo(linkNode);
        PunchCards.setDirty();
        return true;
    }

    public void linkEffects(World worldIn, BlockPos iPos, BlockState stateIn) {
        getNode(worldIn,stateIn,iPos).createLinkEffect(worldIn);
    }

    public boolean hasDirectPower(BlockState state, World worldIn, BlockPos pos) {
        return getNode(worldIn,state,pos).hasDirectPowerMarked(pos);
    }

    public void playClickOnSound(IWorld iWorldIn, BlockPos pos) {
        if (!(iWorldIn instanceof World)) { return; } // Assuming it's always an instanceof, but needed to safely cast.
        final World worldIn = (World) iWorldIn;
        getNode(worldIn,worldIn.getBlockState(pos),pos).playClickOnSound(worldIn,pos);
    }

    public void playClickOffSound(IWorld iWorldIn, BlockPos pos) {
        if (!(iWorldIn instanceof World)) { return; } // Assuming it's always an instanceof, but needed to safely cast.
        final World worldIn = (World) iWorldIn;
        getNode(worldIn,worldIn.getBlockState(pos),pos).playClickOffSound(worldIn,pos);
    }

    public void toggleMuffler(World worldIn, BlockPos pos) {
        getNode(worldIn,worldIn.getBlockState(pos),pos).toggleMuffler();
    }

    public void toggleInverted(World worldIn, BlockPos pos) {
        getNode(worldIn,worldIn.getBlockState(pos),pos).toggleInverted();
    }

    public void clearPlayerList(World worldIn, BlockPos pos) {
        getNode(worldIn,worldIn.getBlockState(pos),pos).clearPlayerList();
    }

}
