package zed.d0c.floormats.items;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import zed.d0c.floormats.blocks.AbstractFloorMatBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static zed.d0c.floormats.blocks.AbstractFloorMatBlock.Sensitivity.*;

public class FloorMatItem extends BlockItem {

    public FloorMatItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player==null) { return; }
        Block block = this.getBlock();
        if (!(block instanceof AbstractFloorMatBlock)) { return; } // this should never happen, but wanted safe casting
        AbstractFloorMatBlock floorMatBlock = (AbstractFloorMatBlock) block;
        if (Screen.hasShiftDown()) {
            AbstractFloorMatBlock.Sensitivity sensitivity = floorMatBlock.sensitivity;
            ITextComponent trigger;
            switch (sensitivity) {
                case EVERYTHING:
                    trigger = new TranslationTextComponent("info.floormats.trigger.everything");
                    break;
                case MOBS:
                    trigger = new TranslationTextComponent("info.floormats.trigger.mobs");
                    break;
                case ENTITIES:
                    trigger = new TranslationTextComponent("info.floormats.trigger.entities");
                    break;
                case PLAYERS:
                    trigger = new TranslationTextComponent("info.floormats.trigger.players");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + sensitivity);
            }
            tooltip.add(trigger);
            if (Screen.hasControlDown()) {
                ITextComponent description = floorMatBlock.getToolTipText();
                if ((description.getStyle().getColor() == null) ||
                        (description.getStyle().getColor() == Color.fromTextFormatting(TextFormatting.WHITE))) {
                    description.getStyle().mergeStyle(description.getStyle().setColor(Color.fromTextFormatting(TextFormatting.GRAY)));
                }
                tooltip.add(description);
            } else {
                tooltip.add(new TranslationTextComponent("tooltip.ctrl_shift"));
            }
            return;
        }
        tooltip.add(new TranslationTextComponent("tooltip.ctrl_shift"));
    }

}
