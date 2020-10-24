package zed.d0c.floormats.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;

public class Command_Help_Topic implements Command<CommandSource> {

    private static final Command_Help_Topic CMD = new Command_Help_Topic();

    // Parameter 'dispatcher' is never used
    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        final String[] topics = {"connections","gold","linking","power","types","wiki"};
        return Commands.literal("help")
                .executes(Command_Help.register(dispatcher).getCommand())
                .then(  Commands.argument("topic", StringArgumentType.string())
                        .suggests((ctx,builder)-> ISuggestionProvider.suggest(topics,builder))
                        .executes(CMD) );
    }

    // Exception 'com.mojang.brigadier.exceptions.CommandSyntaxException' is never thrown in the method
    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String topic = context.getArgument("topic",String.class);
        switch (topic) {
            case "connections":
            case "gold":
            case "linking":
            case "power":
            case "types":
            case "wiki":
                context.getSource().sendFeedback(ForgeHooks.newChatWithLinks(new TranslationTextComponent("command.floormats.help."+topic).getUnformattedComponentText()), false);
                break;
            default:
                context.getSource().sendFeedback(new TranslationTextComponent("command.floormats.help.topics"), false);
                break;
        }
        return 0;
    }
}