package zed.d0c.floormats.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zed.d0c.floormats.FloorMats;

import java.util.Objects;

import static zed.d0c.floormats.FloorMats.MODID;

public class ModCommands {

    // Variable 'cmd' is never used
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> cmd = dispatcher.register(
                Commands.literal(FloorMats.MODID)
                        .executes(Command_None.register(dispatcher).getCommand())
                        .then(Command_Help_Topic.register(dispatcher))
                        .then(Command_Modifiers.register(dispatcher))
                        .then(Command_Reset.register(dispatcher))
                        .then(Command_Tools.register(dispatcher))
        );

        // Don't see any reason to alias the command, /floormats is fine.
        // dispatcher.register(Commands.literal("mats").redirect(cmd));
    }

    protected static void listTagItems(CommandContext<CommandSource> context, String translationKey, String itemTagString) {
        ITag<Item> tag;
        context.getSource().sendFeedback(new TranslationTextComponent(translationKey), false);
        tag = ItemTags.getCollection().get(itemTagString.contains(":")? new ResourceLocation(itemTagString): new ResourceLocation(MODID, itemTagString));
        if (tag == null) {
            context.getSource().sendFeedback(new TranslationTextComponent("command.floormats.tag_disabled"), false);
        } else {
            for (Item entry : tag.getAllElements()) {
                context.getSource().sendFeedback(new StringTextComponent(Objects.requireNonNull(entry.getRegistryName()).toString()), false);
            }
        }
    }

}
