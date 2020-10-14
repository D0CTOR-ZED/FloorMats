package zed.d0c.floormats.blocks.floormats;

import net.minecraft.block.material.Material;
import net.minecraft.block.SoundType;
import net.minecraftforge.common.ToolType;
import zed.d0c.floormats.blocks.AbstractFloorMatBlock;

public class Oak_FloorMat_Block extends AbstractFloorMatBlock {

    public Oak_FloorMat_Block() {
        super(  Sensitivity.EVERYTHING,
                Properties.create(Material.WOOD)
                        .sound(SoundType.WOOD)
                        .hardnessAndResistance(1.0f)
                        .lightValue(0)
                        .harvestLevel(0)
                        .harvestTool(ToolType.AXE)
        );
    }
}
