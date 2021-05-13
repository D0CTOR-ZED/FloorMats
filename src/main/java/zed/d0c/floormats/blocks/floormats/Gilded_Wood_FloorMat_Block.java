package zed.d0c.floormats.blocks.floormats;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;

public class Gilded_Wood_FloorMat_Block extends Gold_FloorMat_Block {

    public Gilded_Wood_FloorMat_Block() {
        super(Sensitivity.PLAYERS,
                Properties.create(Material.WOOD, MaterialColor.GOLD)
                        .sound(SoundType.WOOD)
                        .hardnessAndResistance(1.0f)
                        .harvestLevel(0)
                        .harvestTool(ToolType.AXE)
        );
    }

}