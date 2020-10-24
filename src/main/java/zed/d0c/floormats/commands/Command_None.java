package zed.d0c.floormats.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class Command_None implements Command<CommandSource> {

    private static final Command_None CMD = new Command_None();

    // Parameter 'dispatcher' is never used
    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    // Exception 'com.mojang.brigadier.exceptions.CommandSyntaxException' is never thrown in the method
    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(new TranslationTextComponent("command.floormats"), false);
        return 0;
    }
}