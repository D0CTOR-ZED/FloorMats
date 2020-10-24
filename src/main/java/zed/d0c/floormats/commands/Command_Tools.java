package zed.d0c.floormats.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
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
        for (Item entry : ItemTags.getCollection().getOrCreate(new ResourceLocation(MODID, "connectors")).getAllElements() ) {
            context.getSource().sendFeedback(new TranslationTextComponent("").appendText(Objects.requireNonNull(entry.getRegistryName()).toString()),false);
        }
        context.getSource().sendFeedback(new TranslationTextComponent("command.floormats.tools.wands"), false);
        for (Item entry : ItemTags.getCollection().getOrCreate(new ResourceLocation(MODID, "linkers")).getAllElements() ) {
            context.getSource().sendFeedback(new TranslationTextComponent("").appendText(Objects.requireNonNull(entry.getRegistryName()).toString()),false);
        }
        return 0;
    }
}