package zed.d0c.floormats.blocks.floormats;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import zed.d0c.floormats.blocks.AbstractFloorMatBlock;

public class Stone_FloorMat_Block extends AbstractFloorMatBlock {

    public Stone_FloorMat_Block() {
        super(  Sensitivity.MOBS,
                Properties.create(Material.ROCK)
                        .sound(SoundType.WOOD)
                        .hardnessAndResistance(0.5f)
                        .harvestLevel(0)
                        .harvestTool(ToolType.PICKAXE)
        );
    }
}
