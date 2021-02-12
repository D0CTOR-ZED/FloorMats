package zed.d0c.floormats.blocks.floormats;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import zed.d0c.floormats.blocks.AbstractFloorMatBlock;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class Gilded_Wood_FloorMat_Block extends Gold_FloorMat_Block {

    public Gilded_Wood_FloorMat_Block() {
        super(Sensitivity.PLAYERS,
                Properties.create(Material.WOOD, MaterialColor.GOLD)
                        .sound(SoundType.WOOD)
                        .hardnessAndResistance(1.0f)
                        .lightValue(0)
                        .harvestLevel(0)
                        .harvestTool(ToolType.AXE)
        );
    }

}