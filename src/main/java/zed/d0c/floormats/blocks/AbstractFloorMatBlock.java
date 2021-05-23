/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Class: AbstractFloorMatBlock
 *      Multiblock pressure plates.
 *  Copyright (c) 2020 D0CTOR ZED
 *  This code is licensed under the MIT License, available in the root folder.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package zed.d0c.floormats.blocks;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import zed.d0c.clusters.Clusters;
import zed.d0c.floormats.setup.Registration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static net.minecraft.block.SixWayBlock.FACING_TO_PROPERTY_MAP;
import static net.minecraft.util.Hand.MAIN_HAND;
import static zed.d0c.floormats.FloorMats.MODID;

@ParametersAreNonnullByDefault
public abstract class AbstractFloorMatBlock extends AbstractPressurePlateBlock implements IWaterLoggable {

    // Clusters is a class that manages the connected status of the floormats.
    protected static final Clusters FloorMatClusters = new Clusters();

    public final AbstractFloorMatBlock.Sensitivity sensitivity;

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final HashMap<PlayerEntity,BlockPos> toolUsedPosition = new HashMap<>();
    private static final HashMap<PlayerEntity,ItemStack> toolUsedStack = new HashMap<>();
    private static final HashMap<PlayerEntity,BlockPos> linkUsedPosition = new HashMap<>();
    protected static final Random random = new Random();

    protected AbstractFloorMatBlock(AbstractFloorMatBlock.Sensitivity sensitivityIn, Block.Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState()
                .with(BlockStateProperties.NORTH, false)
                .with(BlockStateProperties.SOUTH, false)
                .with(BlockStateProperties.WEST, false)
                .with(BlockStateProperties.EAST, false)
                .with(POWERED, false)
                .with(WATERLOGGED, false)
        );
        this.sensitivity = sensitivityIn;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(
                BlockStateProperties.NORTH,
                BlockStateProperties.SOUTH,
                BlockStateProperties.WEST,
                BlockStateProperties.EAST,
                POWERED,
                WATERLOGGED
        );
    }

    @Override
    @Nonnull
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        // This is where pressure plates would depress.  Floor mats do not depress.
        return Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    }

    protected void playClickOnSound(IWorld worldIn, BlockPos pos) {
        FloorMatClusters.playClickOnSound(worldIn,pos);
    }

    protected void playClickOffSound(IWorld worldIn, BlockPos pos) {
        FloorMatClusters.playClickOffSound(worldIn,pos);
    }

    protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
        BlockPos iPos = pos.toImmutable();
        // still using smaller pressure plate shape.  Not sure if there is a reason they made plates slightly smaller.
        AxisAlignedBB axisalignedbb = PRESSURE_AABB.offset(iPos);

        int redstoneStrength = 0;
        ArrayList<PlayerEntity> playerList = new ArrayList<>();

        switch (this.sensitivity) {
            case EVERYTHING: // based on wooden pressure plates
                for (Entity entity : worldIn.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb)) {
                    if (!entity.doesEntityNotTriggerPressurePlate()) {
                        redstoneStrength = 15;
                        break;
                    }
                }
                break;
            case MOBS: // based on stone pressure plates
                for (Entity entity : worldIn.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb)) {
                    if (!entity.doesEntityNotTriggerPressurePlate()) {
                        redstoneStrength = 15;
                        break;
                    }
                }
                break;
            case ENTITIES: // based on weighted pressure plate (metal), where they don't check doesEntityNotTriggerPressurePlate
                // ENTITIES isn't currently being used as metal floormats went to PLAYERS.
                if (!worldIn.getEntitiesWithinAABB(Entity.class, axisalignedbb).isEmpty()) {
                    redstoneStrength = 15;
                }
                break;
            case PLAYERS: // custom made to only be triggered by players
                playerList.addAll(worldIn.getEntitiesWithinAABB(PlayerEntity.class, axisalignedbb));
                if (!playerList.isEmpty()) {
                    redstoneStrength = 15;
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.sensitivity);
        }

        if (redstoneStrength != 0) {
            if (FloorMatClusters.applyDirectPower(worldIn, iPos, playerList)) {
                worldIn.getPendingBlockTicks().scheduleTick(iPos, this, this.getPoweredDuration());
            }
        }
        return redstoneStrength;
    }

    protected int getRedstoneStrength(BlockState state) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Nonnull
    protected BlockState setRedstoneStrength(BlockState state, int strength) {
        return state.with(POWERED, strength > 0);
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        // hasNode is needed as work-around for post-existence entity collisions ('this' block already removed) from what seems to be multi-threaded performance enhancements mod Performant
        if ( (!worldIn.isRemote) && FloorMatClusters.hasNode(worldIn,state,pos) && (!FloorMatClusters.hasDirectPower(state, worldIn,pos)) ) {
            computeRedstoneStrength(worldIn, pos);
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (state.get(POWERED)) {
            if (this.computeRedstoneStrength(worldIn, pos) > 0) {
                worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, this.getPoweredDuration());
            } else {
                if (FloorMatClusters.removeDirectPower(state, worldIn, pos)) {
                    playClickOffSound(worldIn, pos);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        // this is called twice per block placement, in Chunk setBlockState and ForgeHooks onPlaceItemIntoWorld
        // It would be nice to be able to get called only once, but I'd also rather not add a processing step
        // to onPlaceItemIntoWorld, since I'm assuming that would require an isItMyBlock on every block ever placed
        // where I hope this is only called when this block type is placed.
        if ((!worldIn.isRemote) && (state.getBlock() != oldState.getBlock())) {
            FloorMatClusters.addToClusters(worldIn, pos, state);
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        final BlockPos iPos = pos.toImmutable();
        if ((!worldIn.isRemote) && (state.getBlock() != newState.getBlock())) {
            FloorMatClusters.removeFromClusters(worldIn, iPos, state);
        }
        super.onReplaced(state, worldIn, iPos, newState, isMoving);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> list, ITooltipFlag flags) {
        list.add(new TranslationTextComponent(""));

        ITextComponent description = new TranslationTextComponent("info.floormats.floormat_block");
        if ( ( description.getStyle().getColor() == null ) ||
                ( description.getStyle().getColor() == Color.fromTextFormatting(TextFormatting.WHITE) ) ) {
            description.getStyle().mergeStyle(description.getStyle().setColor(Color.fromTextFormatting(TextFormatting.GRAY)));
        }

        ITextComponent trigger;
        switch (this.sensitivity) {
            case EVERYTHING:
                trigger = new TranslationTextComponent("info.floormats.trigger.everything");
                break;
            case MOBS:
                trigger = new TranslationTextComponent("info.floormats.trigger.mobs");
                break;
            case ENTITIES:
                trigger = new TranslationTextComponent("info.floormats.trigger.entities");
                break;
            case PLAYERS:
                trigger = new TranslationTextComponent("info.floormats.trigger.players");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.sensitivity);
        }
        list.add(description);
        list.add(trigger);
        // list.add(description.appendText("  ").appendSibling(trigger));
    }

    public boolean canConnect(BlockState state) {
        return this.getBlock().equals(state.getBlock());
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IBlockReader iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockState bSNorth = iblockreader.getBlockState(blockpos.north());
        BlockState bSSouth = iblockreader.getBlockState(blockpos.south());
        BlockState bSWest = iblockreader.getBlockState(blockpos.west());
        BlockState bSEast = iblockreader.getBlockState(blockpos.east());
        Boolean cCNorth = this.canConnect(bSNorth);
        Boolean cCSouth = this.canConnect(bSSouth);
        Boolean cCWest = this.canConnect(bSWest);
        Boolean cCEast = this.canConnect(bSEast);
        Boolean poweredConnection =
                (cCNorth && bSNorth.get(POWERED)) ||
                (cCSouth && bSSouth.get(POWERED)) ||
                (cCWest && bSWest.get(POWERED)) ||
                (cCEast && bSEast.get(POWERED));
        return Objects.requireNonNull(super.getStateForPlacement(context))
                .with(BlockStateProperties.NORTH, cCNorth)
                .with(BlockStateProperties.SOUTH, cCSouth)
                .with(BlockStateProperties.WEST, cCWest)
                .with(BlockStateProperties.EAST, cCEast)
                .with(POWERED, poweredConnection)
                .with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
    }

    @Nonnull
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        if (!facing.getAxis().isHorizontal()) {
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        return stateIn.with(FACING_TO_PROPERTY_MAP.get(facing),(facingState.getBlock() instanceof AbstractFloorMatBlock) && facingState.get(FACING_TO_PROPERTY_MAP.get(facing.getOpposite())));
    }

    public enum Sensitivity {
        EVERYTHING, // based on wood
        MOBS,       // base on stone
        ENTITIES,   // based on metal, not currently used
        PLAYERS     // used by metal
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        return IWaterLoggable.super.receiveFluid(worldIn, pos, state, fluidStateIn);
    }

    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return IWaterLoggable.super.canContainFluid(worldIn, pos, state, fluidIn);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        switch (type) {
            case WATER:
                return worldIn.getFluidState(pos).isTagged(FluidTags.WATER);
            case LAND:
            case AIR:
            default:
                return false;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if ( fromPos.equals(pos.down()) &&
                (!worldIn.isRemote()) &&
                worldIn.isBlockPowered(fromPos) ) {
            // this will pulse the node.  It will turn on, tick, then turn off.
            BlockPos iPos = pos.toImmutable();
            if (FloorMatClusters.applyDirectPower(worldIn, iPos, null)) {
                // 30 = Length of wooden button.  Timing of power should equal or exceed this for aesthetics
                // when placing button under a row of trap doors, otherwise, the door above the button stays open longer
                worldIn.getPendingBlockTicks().scheduleTick(iPos, this, 30);
            }
        }
    }

    // Be mindful that the first pos could have been set in a different dimension.  Linking is not cross-dimensional.
    // Also, first block could have been altered, basically always verify first block is correct.
    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState stateIn, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        if (worldIn.isRemote) return ActionResultType.SUCCESS;
        ItemStack itemInHand = (hand == MAIN_HAND) ? player.inventory.getCurrentItem() : player.inventory.offHandInventory.get(0);
        if ((!isConnector(itemInHand.getItem())) && (!isLinker(itemInHand.getItem()))) {
            return ActionResultType.PASS;
        }
        if (!FloorMatClusters.canAlter(worldIn, pos, stateIn, player.getUniqueID())) {
            worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), Registration.FLOORMATS_DENIED.get(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        }
        ItemStack itemInOppositeHand = (hand != MAIN_HAND) ? player.inventory.getCurrentItem() : player.inventory.offHandInventory.get(0);
        BlockPos iPos = pos.toImmutable();
        if (isConnector(itemInHand.getItem()))
            return useConnector(stateIn, worldIn, player, itemInOppositeHand, iPos, trace);
        if (isLinker(itemInHand.getItem()))
            return useLinker(stateIn, worldIn, player, hand, itemInHand, iPos);
        return ActionResultType.SUCCESS;
    }

    private Direction getHorizontalDirectionFromQuadrant(BlockPos pos, BlockRayTraceResult trace) {
        final double hitX = trace.getHitVec().x - pos.getX() - 0.5;
        final double hitY = trace.getHitVec().y - pos.getY() - 0.5;
        final double hitZ = trace.getHitVec().z - pos.getZ() - 0.5;
        switch (trace.getFace()) {
            case UP:
            case DOWN: return (Math.abs(hitX)>Math.abs(hitZ))
                    ?   ( (hitX>0) ? Direction.EAST : Direction.WEST )
                    :   ( (hitZ>0) ? Direction.SOUTH : Direction.NORTH );
            case NORTH:
            case SOUTH: return ( (hitX>0) ? Direction.EAST : Direction.WEST );
            case EAST:
            case WEST: return ( (hitZ>0) ? Direction.SOUTH : Direction.NORTH );
            default:
                throw new IllegalStateException("Unexpected value: " + trace.getFace());
        }
    }

    private boolean isConnector(Item item) {
        ITag<Item> tag = ItemTags.getCollection().get(new ResourceLocation(MODID, "connectors"));
        return ( (tag != null) && tag.contains(item) );
    }

    private boolean isLinker(Item item) {
        ITag<Item> tag = ItemTags.getCollection().get(new ResourceLocation(MODID, "linkers"));
        return ( (tag != null) && tag.contains(item) );
    }

    private boolean hasTag(Item item, ResourceLocation tagRL) {
        ITag<Item> tag = ItemTags.getCollection().get(tagRL);
        return ( (tag != null) && tag.contains(item) );
    }

    @NotNull
    private ActionResultType useConnector(BlockState stateIn, World worldIn, PlayerEntity player, ItemStack offHand, BlockPos iPos, BlockRayTraceResult trace) {
        worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), Registration.FLOORMATS_WRENCHED.get(), SoundCategory.NEUTRAL, 0.5F, 0.8F / (random.nextFloat() * 0.4F + 0.8F));

        if (hasTag(offHand.getItem(),new ResourceLocation(MODID, "muffler"))) {
            FloorMatClusters.toggleMuffler(worldIn, iPos);
            return ActionResultType.SUCCESS;
        }

        Direction direction = getHorizontalDirectionFromQuadrant(iPos,trace);
        BlockPos adjacentPos = iPos.offset(direction);
        BlockState oldAdjBS = worldIn.getBlockState(adjacentPos);
        if (worldIn.getBlockState(adjacentPos).getBlock() != stateIn.getBlock()) {
            return ActionResultType.SUCCESS;
        }
        BooleanProperty directionProp = FACING_TO_PROPERTY_MAP.get(direction);
        BooleanProperty oppositeDirectionProp = FACING_TO_PROPERTY_MAP.get(direction.getOpposite());
        BlockState newState = stateIn.with(directionProp,!stateIn.get(directionProp));
        BlockState newAdjBS = oldAdjBS.with(oppositeDirectionProp,oldAdjBS.get(oppositeDirectionProp));
        worldIn.setBlockState(adjacentPos, newAdjBS, Constants.BlockFlags.BLOCK_UPDATE); // BLOCK_UPDATE to send changes to clients
        worldIn.markBlockRangeForRenderUpdate(iPos, oldAdjBS, newAdjBS);
        worldIn.setBlockState(iPos, newState, Constants.BlockFlags.BLOCK_UPDATE); // BLOCK_UPDATE to send changes to clients
        worldIn.markBlockRangeForRenderUpdate(iPos, stateIn, newState);
        if (newState.get(directionProp)) {
            FloorMatClusters.addToClusters(worldIn, iPos, newState);
        } else {
            FloorMatClusters.alterClusters(worldIn, iPos, newState);
        }
        return ActionResultType.SUCCESS;
    }

    @NotNull
    private ActionResultType useLinker(BlockState stateIn, World worldIn, PlayerEntity player, Hand hand, ItemStack itemInHand, BlockPos iPos) {
        FloorMatClusters.linkEffects(worldIn, iPos, stateIn);
        final float pitch = 0.8F / (random.nextFloat() * 0.4F + 0.8F);
        if (linkUsedPosition.containsKey(player)) {
            BlockPos firstPos = linkUsedPosition.get(player);
            BlockState firstBS = worldIn.getBlockState(firstPos);
            if ((firstBS.getBlock() instanceof AbstractFloorMatBlock)
                    && FloorMatClusters.canAlter(worldIn, firstPos, firstBS, player.getUniqueID())) {
                FloorMatClusters.linkEffects(worldIn,firstPos,firstBS);
                if ( FloorMatClusters.linkClusters(worldIn, iPos, stateIn, firstPos, firstBS) ) {
                    if (!player.abilities.isCreativeMode) {
                        if (itemInHand.getMaxStackSize() > 1) {
                            itemInHand.shrink(1);
                        } else if (itemInHand.attemptDamageItem(1, worldIn.rand, (ServerPlayerEntity) player)) {
                            itemInHand.setCount(0);
                        }
                        if (hand == MAIN_HAND) {
                            player.inventory.mainInventory.set(player.inventory.currentItem, itemInHand);
                        } else {
                            player.inventory.offHandInventory.set(0, itemInHand);
                        }
                    }
                    worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), Registration.FLOORMATS_LINKED.get(), SoundCategory.NEUTRAL, 0.5F, pitch + 0.4F);
                } else {
                    worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), Registration.FLOORMATS_UNLINKED.get(), SoundCategory.NEUTRAL, 0.5F, pitch + 0.4F);
                }
                linkUsedPosition.remove(player);
            } else {
                linkUsedPosition.put(player, iPos);
                worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), Registration.FLOORMATS_MARKED.get(), SoundCategory.NEUTRAL, 0.5F, pitch);
            }
        } else {
            linkUsedPosition.put(player, iPos);
            worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), Registration.FLOORMATS_MARKED.get(), SoundCategory.NEUTRAL, 0.5F, pitch);
        }
        player.getCooldownTracker().setCooldown(itemInHand.getItem(), 20);
        player.addStat(Stats.ITEM_USED.get(itemInHand.getItem()));
        return ActionResultType.SUCCESS;
    }

}