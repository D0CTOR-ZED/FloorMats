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
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
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
        if (this.material.equals(Material.WOOD)) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
        } else if (this.material.equals(Material.ROCK)) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
        } else if (this.material.equals(Material.IRON)) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.90000004F);
        }
    }

    protected void playClickOffSound(IWorld worldIn, BlockPos pos) {
        if (this.material.equals(Material.WOOD)) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.8F);
        } else if (this.material.equals(Material.ROCK)) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.6F);
        } else if (this.material.equals(Material.IRON)) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.90000004F);
        }
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
                worldIn.getPendingBlockTicks().scheduleTick(iPos, this, this.tickRate(worldIn));
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
        if ( (!worldIn.isRemote) && (!FloorMatClusters.hasDirectPower(state, worldIn,pos)) ) {
            computeRedstoneStrength(worldIn, pos);
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (state.get(POWERED)) {
            if (this.computeRedstoneStrength(worldIn, pos) > 0) {
                worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, this.tickRate(worldIn));
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
                ( description.getStyle().getColor() == TextFormatting.WHITE ) ) {
            description.setStyle(new Style().setColor(TextFormatting.GRAY));
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
        list.add(description.appendText("  ").appendSibling(trigger));
    }

    public boolean canConnect(BlockState state) {
        return this.getBlock().equals(state.getBlock());
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IBlockReader iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
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
                .with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
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
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
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
        ItemStack itemInHand = (hand == MAIN_HAND) ? player.inventory.getCurrentItem() : player.inventory.offHandInventory.get(0);
        if ((worldIn.isRemote)) {
            if (itemInHand.getItem() instanceof EnderPearlItem) {
                BlockPos iPos = pos.toImmutable();
                double x = iPos.getX();
                double y = iPos.getY();
                double z = iPos.getZ();
                for (int i = 0; i < 32; ++i) {
                    worldIn.addParticle(ParticleTypes.PORTAL, x + random.nextDouble(), y + random.nextDouble() * 0.4D, z + random.nextDouble(), random.nextGaussian(), 0.0D, random.nextGaussian());
                }
            }
        }
        if (!worldIn.isRemote) {
            final float pitch = 0.8F / (random.nextFloat() * 0.4F + 0.8F);
            if (FloorMatClusters.canAlter(worldIn, pos, stateIn, player.getUniqueID())) {
                BlockPos iPos = pos.toImmutable();
                if (isConnector(itemInHand.getItem())) {
                    worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), Registration.FLOORMATS_WRENCHED.get(), SoundCategory.NEUTRAL, 0.5F, pitch );
                    if ((toolUsedPosition.containsKey(player)) && (toolUsedStack.get(player).equals(itemInHand))) {
                        BlockPos firstPos = toolUsedPosition.get(player);
                        BlockState oldFirstBS = worldIn.getBlockState(firstPos);
                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            BooleanProperty directionProperty = FACING_TO_PROPERTY_MAP.get(direction);
                            if ((iPos.offset(direction).equals(firstPos)) && (worldIn.getBlockState(firstPos).getBlock() == stateIn.getBlock())) {
                                BlockState newState = stateIn.cycle(directionProperty);
                                BlockState newFirstBS = oldFirstBS.cycle(FACING_TO_PROPERTY_MAP.get(direction.getOpposite()));
                                worldIn.setBlockState(firstPos, newFirstBS, Constants.BlockFlags.BLOCK_UPDATE); // BLOCK_UPDATE to send changes to clients
                                worldIn.markBlockRangeForRenderUpdate(iPos, oldFirstBS, newFirstBS);
                                worldIn.setBlockState(iPos, newState, Constants.BlockFlags.BLOCK_UPDATE); // BLOCK_UPDATE to send changes to clients
                                worldIn.markBlockRangeForRenderUpdate(iPos, stateIn, newState);
                                toolUsedPosition.remove(player);
                                toolUsedStack.remove(player);
                                if (newState.get(directionProperty)) {
                                    FloorMatClusters.addToClusters(worldIn, iPos, newState);
                                } else {
                                    FloorMatClusters.alterClusters(worldIn, iPos, newState);
                                }
                                return ActionResultType.SUCCESS;
                            }
                        }
                    }
                    toolUsedPosition.put(player, iPos);
                    toolUsedStack.put(player, itemInHand);
                    return ActionResultType.SUCCESS;
                }
                if (isLinker(itemInHand.getItem())) {
                    if (linkUsedPosition.containsKey(player)) {
                        BlockPos firstPos = linkUsedPosition.get(player);
                        BlockState firstBS = worldIn.getBlockState(firstPos);
                        if ((firstBS.getBlock() instanceof AbstractFloorMatBlock)
                                && FloorMatClusters.canAlter(worldIn, firstPos, firstBS, player.getUniqueID())) {
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
            } else {
                if (isLinker(itemInHand.getItem())) {
                    worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), Registration.FLOORMATS_DENIED.get(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    private boolean isConnector(Item item) {
        return  (ItemTags.getCollection().getOrCreate(new ResourceLocation(MODID, "connectors")).contains(item));
    }

    private boolean isLinker(Item item) {
        return  (ItemTags.getCollection().getOrCreate(new ResourceLocation(MODID, "linkers")).contains(item));
    }

}