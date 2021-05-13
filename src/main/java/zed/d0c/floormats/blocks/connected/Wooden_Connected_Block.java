package zed.d0c.floormats.blocks.connected;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import zed.d0c.floormats.blocks.AbstractConnectedBlock;

public class Wooden_Connected_Block extends AbstractConnectedBlock {

    public Wooden_Connected_Block() {
        super(  Properties.create(Material.WOOD)
                        .sound(SoundType.WOOD)
                        .hardnessAndResistance(1.0f)
                        .harvestLevel(0)
                        .harvestTool(ToolType.AXE)
        );
    }

}
