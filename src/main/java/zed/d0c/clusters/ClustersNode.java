/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Class: ClustersNode
 *      Part of the Clusters package.  Handles the connected bits.
 *  Copyright (c) 2020 D0CTOR ZED
 *  This code is licensed under the MIT License, available in the root folder.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package zed.d0c.clusters;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import zed.d0c.floormats.blocks.AbstractFloorMatBlock;

import javax.annotation.Nullable;
import java.util.*;

import static net.minecraft.block.SixWayBlock.FACING_TO_PROPERTY_MAP;
import static net.minecraft.state.properties.BlockStateProperties.POWERED;
import static zed.d0c.clusters.Clusters.ClustersSet;

/*  ************************************************************************************
 *
 *  Clusters Node - The data for a single cluster.
 *
 *  Each collection of identical clusters blocks is a single clusters node.
 *
 *  TO DO:
 *
 *  Replace hard coded POWERED with state/function from Clusters registry
 *
 *  Add the ability for sets of blocks to form a single cluster.  At the
 *  moment, each cluster is a single block type.
 *
 *  ************************************************************************************
 *  CLUSTER_NODE COMPOUND NBT STRUCTURE
 *
 *  Structure           KEY                 Data Type
 *  ------------        ----------------    --------------------------------------------
 *  BLOCK_TYPE          BLOCK_TYPE_KEY      String
 *  ID                  ID_KEY              UUID
 *  LINK                LINK_KEY            UUID
 *  LINK_BACK           LINK_BACK_KEY       UUID
 *  UUID_LIST           LIST_UUID_KEY       ListNBT of CompoundNBT (UniqueIDs)
 *  LOWER_BOUND         LOWER_BOUND_KEY     Long (NOT IMPLEMENTED)
 *  UPPER_BOUND         UPPER_BOUND_KEY     Long (NOT IMPLEMENTED)
 *  NODE_MAP            NODE_MAP_KEY        CompoundNBT (Key: BLOCK_POS, Value: BOOLEAN)
 *      BLOCK_POS       N/A                 String (BlockPos.toLong.toString)
 *      DIRECT_POWER    BLOCK_POS           boolean (Indicates directly powered)
 *  ************************************************************************************/

// Public due to PunchCards using it in their read/writes.
// Perhaps PunchCards should access node data through Clusters?
public class ClustersNode implements INBTSerializable<CompoundNBT> {

    private final Block cnBlock;
    private final HashSet<UUID> cnUUID_Set = new HashSet<>();
    private final HashMap<BlockPos, Boolean> cnNodeMap = new HashMap<>();
    private UUID cnID;
    UUID cnLink;
    private UUID cnLinkBack;
    // private final HashMap<UUID,Long> accessLog = new HashMap<>();

    static final HashMap<UUID, ClustersNode> idRegistry = new HashMap<>();

    private static final String BLOCK_TYPE_KEY      = "cnBlockName";
    private static final String NODE_MAP_KEY        = "cnNodeMap";
    private static final String LIST_UUID_KEY       = "cnListUUID"; // player list
    private static final String ID_KEY              = "cnID"; // only assigned when needed
    private static final String LINK_KEY            = "cnLink";
    private static final String LINK_BACK_KEY       = "cnLinkBack";
    // private static final String ACCESS_LOG_KEY      = "cnAccessLog";
    protected static final Random random = new Random();

    ClustersNode(Block blockType, HashSet<UUID> uuidSet) {
        cnBlock       = blockType;
        cnUUID_Set.addAll(uuidSet);
    }

    ClustersNode(Block block, BlockPos pos) {
        BlockPos iPos = pos.toImmutable();
        cnBlock = block;
        cnNodeMap.put(iPos,false);
    }

    public ClustersNode(CompoundNBT entry) {
        // cnBlock must be init here, 'cause final.  The rest is deserialized.
        cnBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(entry.getString(BLOCK_TYPE_KEY)));
        deserializeNBT(entry);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        synchronized (this) {
            nbt.putString(BLOCK_TYPE_KEY, Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(cnBlock)).toString());
            if (cnID != null) {
                nbt.putUniqueId(ID_KEY, cnID);
            }
            if (cnLink != null) {
                nbt.putUniqueId(LINK_KEY, cnLink);
                nbt.putUniqueId(LINK_BACK_KEY, cnLinkBack);
            }
            if (!cnUUID_Set.isEmpty()) {
                ListNBT uuidLNBT = new ListNBT();
                for (UUID u : cnUUID_Set) {
                    CompoundNBT cNBT = new CompoundNBT();
                    cNBT.putUniqueId("",u);
                    uuidLNBT.add(cNBT);
                }
                nbt.put (LIST_UUID_KEY, uuidLNBT);
            }
            CompoundNBT nodeMapNBT = new CompoundNBT();
            for (BlockPos key : cnNodeMap.keySet()) {
                nodeMapNBT.putBoolean(Long.toString(key.toLong()), cnNodeMap.get(key));
            }
            nbt.put(NODE_MAP_KEY,nodeMapNBT);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT entry) {
        if (entry.hasUniqueId(ID_KEY)) {
            cnID = entry.getUniqueId(ID_KEY);
            idRegistry.put(cnID,this);
        }
        if (entry.hasUniqueId(LINK_KEY)) {
            cnLink = entry.getUniqueId(LINK_KEY);
            cnLinkBack = entry.getUniqueId(LINK_BACK_KEY);
        }
        final ListNBT uuidLNBT = entry.getList(LIST_UUID_KEY, Constants.NBT.TAG_COMPOUND);
        for (int index=0; index<uuidLNBT.size(); ++index) {
            cnUUID_Set.add(uuidLNBT.getCompound(index).getUniqueId(""));
        }
        CompoundNBT nodeMapNBT = entry.getCompound(NODE_MAP_KEY);
        for (String key : nodeMapNBT.keySet()) {
            cnNodeMap.put(BlockPos.fromLong(Long.parseLong(key)),nodeMapNBT.getBoolean(key));
        }

    }

    public Block getNodeBlockType() { return cnBlock; }

    private UUID getID() {
        if (cnID != null) { return cnID; }
        cnID = UUID.randomUUID();
        idRegistry.put(cnID,this);
        return cnID;
    }

    public boolean isEmpty() { return cnNodeMap.isEmpty(); }

    public int getSize() { return cnNodeMap.keySet().size(); }

    boolean contains(Block block, BlockPos pos) {
        // this is where a check for bounds might prevent searching
        // through the keys.  Not sure if this would improve performance for larger clusters
        // or just add a performance hit with extra checks.
        if ( block == cnBlock ) {
            return cnNodeMap.containsKey(pos);
        }
        return false;
    }

    private static class loopParser {
        private final HashSet<ClustersNode> usedNodes = new HashSet<>();
        private ClustersNode cursor;
        loopParser(ClustersNode start) {
            cursor=start;
        }
        ClustersNode get() {
            return cursor;
        }
        boolean next() {
            usedNodes.add(cursor);
            if (cursor.cnLink != null) {
                cursor = idRegistry.get(cursor.cnLink);
            }
            return !usedNodes.contains(cursor);
        }
    }

    private void broadcastToNeighbors(World worldIn, HashSet<BlockPos> posToBroadcast) {
        // give one chance to cancel this... not sure why this would ever happen.
        // Don't think it needs to be checked for every single iteration.
        BlockPos anyPos = (BlockPos) posToBroadcast.toArray()[0];
        if (net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(worldIn, anyPos, worldIn.getBlockState(anyPos), java.util.EnumSet.allOf(Direction.class), false).isCanceled())
            return;

        for (BlockPos pos : posToBroadcast) {
            BlockState state = worldIn.getBlockState(pos);
            // Is there an better alternative to mass notify.... probably not.
            // I've considered skipping horizontal notification for mats that connect to 4 other mats
            // or on a connected side by side basis, but more checks would probably be worse than
            // a bunch of empty calls. But it's not empty anymore, so maybe processing time checks
            // would indicate which is better for performance.  Really only matters if someone goes
            // crazy with these things.
            Block block = state.getBlock();
            worldIn.neighborChanged(pos.west(), block, pos);
            worldIn.neighborChanged(pos.east(), block, pos);
            worldIn.neighborChanged(pos.down(), block, pos);
            worldIn.neighborChanged(pos.up(), block, pos);
            worldIn.neighborChanged(pos.north(), block, pos);
            worldIn.neighborChanged(pos.south(), block, pos);

            BlockPos downPos = pos.down();
            BlockState downState = worldIn.getBlockState(downPos);
            Block downBlock = downState.getBlock();
            worldIn.neighborChanged(downPos.west(), downBlock, downPos);
            worldIn.neighborChanged(downPos.east(), downBlock, downPos);
            worldIn.neighborChanged(downPos.down(), downBlock, downPos);
            // prevent block under map from telling mat it changed.
            // this is used to identify when a different source powers mat from below.
            // That causes the mat to pulse, for useful redstone mechanics.
            // worldIn.neighborChanged(downPos.up(), downBlock, downPos);
            worldIn.neighborChanged(downPos.north(), downBlock, downPos);
            worldIn.neighborChanged(downPos.south(), downBlock, downPos);

        }
    }

    // returns whether this call caused the iPos to be marked as an actively powered block
    // this will result in the block ticking until it is no longer directly powered
    public boolean powerNode(World worldIn, @Nullable BlockPos iPos, @Nullable ArrayList<PlayerEntity> playerList) {
        // We only need to act if the node wasn't already marked as directly powered.
        if ( (iPos == null) || (!cnNodeMap.get(iPos)) ) {
            if ( (playerList != null) && (!cnUUID_Set.isEmpty()) ) {
                boolean playerRegistered = false;
                for (PlayerEntity player : playerList) {
                    if (cnUUID_Set.contains(player.getUniqueID())) {
                        playerRegistered = true;
                        break;
                    }
                }
                if (!playerRegistered) {
                    return false;
                }
            }

            /* Was getting concurrent modification issues here
             * Now storing key set at start and moved notify loop outside of sync
             * notification was resulting in concurrent modification, probably same thread too
             */
            HashSet<BlockPos> posToUpdate = new HashSet<>(cnNodeMap.keySet());
            synchronized (this) {
                boolean alreadyPowered = isPowered();
                if (iPos != null) {
                    cnNodeMap.put(iPos, true);
                }
                if (!alreadyPowered) {
                    loopParser loop = new loopParser(this);
                    do {
                        HashSet<BlockPos> posToPower = new HashSet<>(loop.get().cnNodeMap.keySet());
                        posToUpdate.addAll(posToPower);
                        for (BlockPos activatePos : posToPower) {
                            BlockState state = worldIn.getBlockState(activatePos);
                            // else added to patch issue #1
                            if (state.getBlock() instanceof AbstractFloorMatBlock) {
                                worldIn.setBlockState(activatePos, state.with(POWERED, true), Constants.BlockFlags.BLOCK_UPDATE); // BLOCK_UPDATE to send changes to clients
                            } else { // This should never happen
                                loop.get().cnNodeMap.remove(activatePos);
                            }
                        }
                    } while (loop.next());
                }
            }
            // custom version of notifyNeighborsOfStateChange
            broadcastToNeighbors(worldIn,posToUpdate);
            return true;
        }
        return false;
    }

    void depowerNode(World worldIn) {
        HashSet<BlockPos> posToUpdate = new HashSet<>();
        synchronized (this) {
            loopParser loop = new loopParser(this);
            do {
                HashSet<BlockPos> posToDepower = new HashSet<>(loop.get().cnNodeMap.keySet());
                posToUpdate.addAll(posToDepower);
                for (BlockPos deactivatePos : posToDepower) {
                    BlockState oldState = worldIn.getBlockState(deactivatePos);
                    if ( oldState.getBlock() instanceof AbstractFloorMatBlock ) {
                        worldIn.setBlockState(deactivatePos, oldState.with(POWERED, false), Constants.BlockFlags.BLOCK_UPDATE); // BLOCK_UPDATE to send changes to clients
                    } else { // this should never happen
                        loop.get().cnNodeMap.remove(deactivatePos);
                    }
                }
            } while (loop.next());
        }
        broadcastToNeighbors(worldIn,posToUpdate);
    }

    boolean isPowered() {
        loopParser loop = new loopParser(this);
        do {
            if (loop.get().cnNodeMap.containsValue(true)) { return true; }
        } while (loop.next());
        return false;
    }

    // returns whether the node depowered so we can play the deactivate sound.
    boolean depowerBlock(World worldIn, BlockPos pos) {
        cnNodeMap.replace(pos, false);
        if (isPowered()) {
            return false;
        } else {
            depowerNode(worldIn);
            return true;
        }
    }

    void absorbOtherNode(ClustersNode otherNode) {
        synchronized (this) {
            cnUUID_Set.addAll(otherNode.cnUUID_Set);
            cnNodeMap.putAll(otherNode.cnNodeMap);
            if (!isLinked(otherNode)) {
                if ( (cnLink != null) || (otherNode.cnLink != null) ) {
                    linkTo(otherNode);
                }
            }
            if (cnLink != null) {
                idRegistry.get(otherNode.cnLinkBack).cnLink = otherNode.cnLink;
                idRegistry.get(otherNode.cnLink).cnLinkBack = otherNode.cnLinkBack;
                if (cnLink == cnID) {
                    cnID = null; // If I add a second use for the ID, this needs to change
                    cnLink = null;
                    cnLinkBack = null;
                }
            }
        }
    }

    boolean isLinked(ClustersNode otherNode) {
        if ( (cnLink == null) || (otherNode.cnLink == null) ) { return false; }
        UUID otherID = otherNode.getID();
        loopParser loop = new loopParser(this);
        do {
            if (loop.get().cnLink.equals(otherID)) { return true; }
        } while (loop.next());
        return false;
    }

    public void removePos(World worldIn, BlockPos pos) {
        Boolean wasPowered;
        synchronized (this) {
            wasPowered = cnNodeMap.get(pos);
            cnNodeMap.remove(pos);
        }
        if (wasPowered && !isPowered()) {
            depowerNode(worldIn);
        }
    }

    /*
     * reformNode is used when a block being removed from a node may cause the node to split into multiple
     * distinct nodes.  This node will have its block entries eliminated as one or more new nodes are formed.
     * Returns a list of newly created distinct nodes
     */
    public ClustersSet reformNode(World worldIn) {

        ClustersSet returnDistinctNodes = new ClustersSet();
        Block blockType = this.getNodeBlockType();

        // This loop will be processed once per contiguous cluster.
        // What it does is it picks a block to start a new cluster,
        // then it identifies adjacent blocks in the same cluster.
        // It keeps adding and checking adjacent position until it
        // has found all of them.  Anything left must be non-contiguous
        // with it, so a new node is started and the process repeats.
        while (cnNodeMap.size() > 0) {
            ClustersNode newNode = new ClustersNode(blockType,cnUUID_Set);
            HashSet<BlockPos> posToAdd = new HashSet<>();

            // We just need any key from the cnNodeMap, so extract the first key
            BlockPos anyKey = ((BlockPos) cnNodeMap.keySet().toArray()[0]);

            // Don't think I need toImmutable, since key should already be immutable
            // and the end result isn't copying the key as much as moving it to a new home
            // not sure and this is an uncommon event.

            posToAdd.add(anyKey.toImmutable());

            while (posToAdd.size() > 0) {
                BlockPos nextPos = (BlockPos) posToAdd.toArray()[0];
                posToAdd.remove(nextPos);

                // move data to newNode, removing it from this.cnNodeMap
                newNode.cnNodeMap.put(nextPos.toImmutable(),this.cnNodeMap.remove(nextPos));
                BlockState state = worldIn.getBlockState(nextPos);
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    if (state.get(FACING_TO_PROPERTY_MAP.get(direction))) {
                        BlockPos adjacentPos = nextPos.offset(direction);
                        if (this.cnNodeMap.containsKey(adjacentPos)) {
                            posToAdd.add(adjacentPos.toImmutable());
                        }
                    }
                }
            }
            returnDistinctNodes.add(newNode);
        }
        if (cnLink != null) {
            for (ClustersNode node : returnDistinctNodes) {
                node.cnLinkBack = cnLinkBack;
                idRegistry.get(cnLinkBack).cnLink = node.getID();
                cnLinkBack = node.cnID;
            }
            idRegistry.get(cnLinkBack).cnLink = cnLink;
            idRegistry.get(cnLink).cnLinkBack =  cnLinkBack;
        }
        return returnDistinctNodes;
    }

    public void powerAsNeeded(World worldIn) {
        if (isPowered()) {
            final HashSet<BlockPos> poweredBPSet = new HashSet<>();
            for (BlockPos pos : cnNodeMap.keySet()) {
                BlockState state = worldIn.getBlockState(pos);
                if (!state.get(POWERED)) {
                    worldIn.setBlockState(pos, state.with(POWERED,true), Constants.BlockFlags.BLOCK_UPDATE); // BLOCK_UPDATE to send changes to clients
                    poweredBPSet.add(pos);
                }
            }
            if (!poweredBPSet.isEmpty()) {
                broadcastToNeighbors(worldIn,poweredBPSet);
            }
        }
    }

    public void addUniqueID(UUID uniqueID) {
        cnUUID_Set.add(uniqueID);
    }

    public boolean canAccess(UUID uniqueID) {
        return ( cnUUID_Set.isEmpty() || cnUUID_Set.contains(uniqueID) );
    }

    public int cmdResetNode(ServerWorld serverWorld) {
        HashSet<BlockPos> failedPosSet = new HashSet<>();
        for (BlockPos posToCheck : cnNodeMap.keySet()) {
            if ( serverWorld.getBlockState(posToCheck).getBlock() != cnBlock ) {
                failedPosSet.add(posToCheck);
            }
        }
        for (BlockPos posToCull : failedPosSet) {
            cnNodeMap.remove(posToCull);
        }
        return failedPosSet.size();
    }

    public void removeLink(World worldIn, boolean wasPowered) {
        if (cnLink != null) {
            idRegistry.get(cnLinkBack).cnLink = cnLink;
            ClustersNode node = idRegistry.get(cnLink);
            node.cnLinkBack = cnLinkBack;
            node.verifyLink();
            wipeLink();
            if (wasPowered && !isPowered()) {
                depowerNode(worldIn);
            } else if (wasPowered && !node.isPowered()) {
                node.depowerNode(worldIn);
            }
        }
    }

    private void wipeLink() {
        cnID = null;
        cnLink = null;
        cnLinkBack = null;
    }

    private void verifyLink() {
        if (cnLink == cnID) {
            wipeLink();
        }
    }

    public void linkTo(ClustersNode linkNode) {
        /*  Possible permutations for this and link:
            -HAS-LOOP-  ----THIS----    ----LINK----
            THIS/LINK   BACK    LINK    BACK    LINK
             not/not    link    link    this    this
            loop/not    same    link    this    t.l*
             not/loop   l.b*    link    this    same
            loop/loop   same    link    this    same    (l.b--t.l)
        */
        if (linkNode.cnLink == null) {
            if (cnLink == null) {
                linkNode.cnLink = getID();
                cnLinkBack = linkNode.getID();
            } else {
                linkNode.cnLink = cnLink;
                idRegistry.get(cnLink).cnLinkBack = linkNode.getID();
            }
        } else {
            if (cnLink == null) {
                cnLinkBack = linkNode.cnLinkBack;
                idRegistry.get(cnLinkBack).cnLink = getID();
            } else {
                idRegistry.get(linkNode.cnLinkBack).cnLink = cnLink;
                idRegistry.get(cnLink).cnLinkBack = linkNode.cnLinkBack;
            }
        }
        cnLink = linkNode.cnID;
        linkNode.cnLinkBack = cnID;
    }

    public void createLinkEffect(World worldIn) {
        for (BlockPos pos : cnNodeMap.keySet()) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            for (int i = 0; i<8; ++i) {
                ((ServerWorld) worldIn).spawnParticle(ParticleTypes.PORTAL, x+random.nextDouble(), y+random.nextDouble(), z+random.nextDouble(), 1, 0.0D, 0.4D, 0.0D, 0.15F);
            }
        }
    }

    public boolean hasDirectPowerMarked(BlockPos pos) {
        return cnNodeMap.get(pos);
    }

}