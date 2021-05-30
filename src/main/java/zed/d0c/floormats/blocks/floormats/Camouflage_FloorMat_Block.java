package zed.d0c.floormats.blocks.floormats;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.NotNull;
import zed.d0c.floormats.blocks.AbstractFloorMatBlock;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

import static net.minecraft.util.math.shapes.VoxelShapes.empty;

public class Camouflage_FloorMat_Block extends Gold_FloorMat_Block {

    public Camouflage_FloorMat_Block() {
        super(  Properties.create(Material.WOOD, MaterialColor.GOLD)
                        .sound(SoundType.WOOD)
                        .hardnessAndResistance(1.0f)
                        .harvestLevel(0)
                        .harvestTool(ToolType.AXE)
                        .notSolid()
                        );
    }

    public static Optional<BlockState> appearanceBlock(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos) {
        return Optional.of(world.getBlockState(pos.down()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockRenderType getRenderType(@NotNull BlockState state) {
        return BlockRenderType.MODEL;
    }

/*
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    @Nonnull
    public VoxelShape getRayTraceShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return empty();
    }
*/

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("deprecation")
    @Override
    @ParametersAreNonnullByDefault
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        // return empty(); // bypass TOP
        return Block.makeCuboidShape(0,0,0,16,1,16);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getRenderShape(@NotNull BlockState state, @NotNull IBlockReader worldIn, @NotNull BlockPos pos) {
        return Block.makeCuboidShape(0,0,0,16,1,16);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull IBlockReader worldIn, @NotNull BlockPos pos) {
        return Block.makeCuboidShape(0,0,0,16,1,16);
    }


}
