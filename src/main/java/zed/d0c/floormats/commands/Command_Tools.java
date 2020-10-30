package zed.d0c.floormats.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Objects;

import static zed.d0c.floormats.FloorMats.MODID;

public class Command_Tools implements Command<CommandSource> {

    private static final Command_Tools CMD = new Command_Tools();

    // Parameter 'dispatcher' is never used
    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("tools")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    // Exception 'com.mojang.brigadier.exceptions.CommandSyntaxException' is never thrown in the method
    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(new TranslationTextComponent("command.floormats.tools.wrenches"), false);
        ITag<Item> tag = ItemTags.getCollection().get(new ResourceLocation(MODID, "connectors"));
        if (tag == null) {
            context.getSource().sendFeedback(new TranslationTextComponent("command.floormats.tools.tag_disabled"), false);
        } else {
            for (Item entry : tag.getAllElements()) {
                context.getSource().sendFeedback(new StringTextComponent(Objects.requireNonNull(entry.getRegistryName()).toString()), false);
            }
        }
        context.getSource().sendFeedback(new TranslationTextComponent("command.floormats.tools.wands"), false);
        tag = ItemTags.getCollection().get(new ResourceLocation(MODID, "linkers"));
        if (tag == null) {
            context.getSource().sendFeedback(new TranslationTextComponent("command.floormats.tools.tag_disabled"), false);
        } else {
            for (Item entry : tag.getAllElements()) {
                context.getSource().sendFeedback(new StringTextComponent(Objects.requireNonNull(entry.getRegistryName()).toString()), false);
            }
        }
        return 0;
    }
}