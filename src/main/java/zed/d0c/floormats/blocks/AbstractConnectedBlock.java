/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Class: AbstractConnectedBlock
 *      Non-functional full-block version of Floor Mats.
 *  Copyright (c) 2020 D0CTOR ZED
 *  This code is licensed under the MIT License, available in the root folder.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package zed.d0c.floormats.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import zed.d0c.floormats.setup.Registration;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

import static net.minecraft.block.SixWayBlock.FACING_TO_PROPERTY_MAP;
import static net.minecraft.util.Hand.MAIN_HAND;
import static zed.d0c.floormats.FloorMats.MODID;

@ParametersAreNonnullByDefault
public abstract class AbstractConnectedBlock extends Block {

    protected static final Random random = new Random();

    public AbstractConnectedBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState()
                .with(BlockStateProperties.UP, true)
                .with(BlockStateProperties.DOWN, true)
                .with(BlockStateProperties.NORTH, true)
                .with(BlockStateProperties.SOUTH, true)
                .with(BlockStateProperties.WEST, true)
                .with(BlockStateProperties.EAST, true)
        );
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(
                BlockStateProperties.UP,
                BlockStateProperties.DOWN,
                BlockStateProperties.NORTH,
                BlockStateProperties.SOUTH,
                BlockStateProperties.WEST,
                BlockStateProperties.EAST
        );
    }

    public boolean canConnect(BlockState state) {
        return this.getBlock().equals(state.getBlock());
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IBlockReader iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        return Objects.requireNonNull(super.getStateForPlacement(context))
                .with(BlockStateProperties.UP, this.canConnect(iblockreader.getBlockState(blockpos.up())))
                .with(BlockStateProperties.DOWN, this.canConnect(iblockreader.getBlockState(blockpos.down())))
                .with(BlockStateProperties.NORTH, this.canConnect(iblockreader.getBlockState(blockpos.north())))
                .with(BlockStateProperties.SOUTH, this.canConnect(iblockreader.getBlockState(blockpos.south())))
                .with(BlockStateProperties.WEST, this.canConnect(iblockreader.getBlockState(blockpos.west())))
                .with(BlockStateProperties.EAST, this.canConnect(iblockreader.getBlockState(blockpos.east())));
    }

    @Nonnull
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return stateIn.with(FACING_TO_PROPERTY_MAP.get(facing),(facingState.getBlock() instanceof AbstractConnectedBlock) && facingState.get(FACING_TO_PROPERTY_MAP.get(facing.getOpposite())));
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState stateIn, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        ItemStack itemInHand = (hand == MAIN_HAND) ? player.inventory.getCurrentItem() : player.inventory.offHandInventory.get(0);
        if (!worldIn.isRemote) {
            final float pitch = 0.8F / (random.nextFloat() * 0.4F + 0.8F);
            BlockPos iPos = pos.toImmutable();
            if (isConnector(itemInHand.getItem())) {
                worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), Registration.FLOORMATS_WRENCHED.get(), SoundCategory.NEUTRAL, 0.5F, pitch );
                final Direction direction = getDirectionFromQuadrant(pos,trace);
                final BooleanProperty directionProperty = FACING_TO_PROPERTY_MAP.get(direction);
                final BlockState newState = stateIn.with(directionProperty,!stateIn.get(directionProperty));
                worldIn.setBlockState(iPos, newState, Constants.BlockFlags.BLOCK_UPDATE); // BLOCK_UPDATE to send changes to clients
                worldIn.markBlockRangeForRenderUpdate(iPos, stateIn, newState);
                return ActionResultType.SUCCESS;
            } else {
                return ActionResultType.PASS;
            }
        }
        return ActionResultType.SUCCESS;
    }

    private Direction getDirectionFromQuadrant(BlockPos pos, BlockRayTraceResult trace) {
        final double hitX = trace.getHitVec().x - pos.getX() - 0.5;
        final double hitY = trace.getHitVec().y - pos.getY() - 0.5;
        final double hitZ = trace.getHitVec().z - pos.getZ() - 0.5;
        switch (trace.getFace()) {
            case UP:
            case DOWN: return (Math.abs(hitX)>Math.abs(hitZ))
                            ?   ( (hitX>0) ? Direction.EAST : Direction.WEST )
                            :   ( (hitZ>0) ? Direction.SOUTH : Direction.NORTH );
            case NORTH:
            case SOUTH: return (Math.abs(hitX)>Math.abs(hitY))
                            ?   ( (hitX>0) ? Direction.EAST : Direction.WEST )
                            :   ( (hitY>0) ? Direction.UP : Direction.DOWN );
            case EAST:
            case WEST: return (Math.abs(hitY)>Math.abs(hitZ))
                            ?   ( (hitY>0) ? Direction.UP : Direction.DOWN )
                            :   ( (hitZ>0) ? Direction.SOUTH : Direction.NORTH );
            default:
                throw new IllegalStateException("Unexpected value: " + trace.getFace());
        }
    }

    private boolean isConnector(Item item) {
        ITag<Item> tag = ItemTags.getCollection().get(new ResourceLocation(MODID, "connectors"));
        return ( (tag != null) && tag.contains(item) );
    }

}