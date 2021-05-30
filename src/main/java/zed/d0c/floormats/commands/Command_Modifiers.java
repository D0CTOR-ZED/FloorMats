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

public class Command_Modifiers implements Command<CommandSource> {

    private static final Command_Modifiers CMD = new Command_Modifiers();

    // Parameter 'dispatcher' is never used
    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("modifiers")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    // Exception 'com.mojang.brigadier.exceptions.CommandSyntaxException' is never thrown in the method
    @SuppressWarnings("RedundantThrows")
    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ModCommands.listTagItems(context,"command.floormats.modifiers.inverters","inverters");
        ModCommands.listTagItems(context,"command.floormats.modifiers.mufflers","mufflers");
        return 0;
    }

}