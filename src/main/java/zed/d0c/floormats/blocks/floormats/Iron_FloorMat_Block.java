package zed.d0c.floormats.blocks.floormats;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import zed.d0c.floormats.blocks.AbstractFloorMatBlock;

public class Iron_FloorMat_Block extends AbstractFloorMatBlock {

    public Iron_FloorMat_Block() {
        super(  Sensitivity.PLAYERS,
                Properties.create(Material.IRON)
                        .sound(SoundType.WOOD)
                        .hardnessAndResistance(1.0f)
                        .lightValue(0)
                        .harvestLevel(0)
                        .harvestTool(ToolType.PICKAXE)
        );
    }
}
