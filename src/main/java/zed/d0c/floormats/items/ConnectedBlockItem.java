package zed.d0c.floormats.items;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ConnectedBlockItem extends BlockItem {

    public ConnectedBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player==null) { return; }
        if (Screen.hasShiftDown() && Screen.hasControlDown()) {
            tooltip.add(new TranslationTextComponent("tooltip.ctrl_shift.connected_block"));
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.ctrl_shift"));
        }
    }

}
