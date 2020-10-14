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
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
import zed.d0c.clusters.Clusters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@ParametersAreNonnullByDefault
public abstract class AbstractFloorMatBlock extends AbstractPressurePlateBlock implements IWaterLoggable {

    // Clusters is a class that manages the connected status of the floormats.
    protected static final Clusters FloorMatClusters = new Clusters();

    public final AbstractFloorMatBlock.Sensitivity sensitivity;

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

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
        // still using smaller pressure plate shape.  Not sure if there is a reason
        // they made plates slightly smaller.
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
        if (!worldIn.isRemote) {
            // Maybe this could be skipped if POWERED and the Node has already marked it as directly powered.
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
        // There is probably a better way to turn the direction into the BlockState property.
        // .... and I've seen the six-way block.  Not sure that would be an improvement.
        BooleanProperty stateDirection;
        switch (facing) {
            case NORTH:
                stateDirection = BlockStateProperties.NORTH;
                break;
            case EAST:
                stateDirection = BlockStateProperties.EAST;
                break;
            case SOUTH:
                stateDirection = BlockStateProperties.SOUTH;
                break;
            case WEST:
                stateDirection = BlockStateProperties.WEST;
                break;
            default:
                return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        return stateIn.with(stateDirection, canConnect(facingState));
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

            // this should pulse the node.  It will turn on, tick, then turn off.
            BlockPos iPos = pos.toImmutable();
            if (FloorMatClusters.applyDirectPower(worldIn, iPos, null)) {
                // 30 = Length of wooden button.  Timing of power should equal or exceed this for aesthetics
                // when placing button under a row of trap doors, otherwise, the door above the button stays open longer
                worldIn.getPendingBlockTicks().scheduleTick(iPos, this, 30);
            }
        }
    }
}