package zed.d0c.floormats.blocks.floormats;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import zed.d0c.floormats.blocks.AbstractFloorMatBlock;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static zed.d0c.floormats.FloorMats.MODID;

public class Gold_FloorMat_Block extends AbstractFloorMatBlock {

    public Gold_FloorMat_Block() {
        super(Sensitivity.PLAYERS,
                Properties.create(Material.IRON, MaterialColor.GOLD)
                        .hardnessAndResistance(1.0f)
                        .harvestLevel(0)
                        .harvestTool(ToolType.PICKAXE)
        );
    }

    public Gold_FloorMat_Block(Properties properties) {
        super(Sensitivity.PLAYERS,properties);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if ( (!worldIn.isRemote) && (placer != null) ) {
            FloorMatClusters.registerUniqueID(worldIn, pos, state, placer.getUniqueID());
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected boolean offHandAction(ItemStack offHandStack, World worldIn, BlockPos iPos) {
        if (hasTag(offHandStack.getItem(),new ResourceLocation(MODID, "inverters"))) {
            FloorMatClusters.toggleInverted(worldIn, iPos);
            return true;
        }
        return super.offHandAction(offHandStack,worldIn,iPos);
    }

}