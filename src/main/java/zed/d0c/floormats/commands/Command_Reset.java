package zed.d0c.floormats.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import zed.d0c.clusters.Clusters;

public class Command_Reset implements Command<CommandSource> {

    private static final Command_Reset CMD = new Command_Reset();

    // Parameter 'dispatcher' is never used
    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("reset")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }

    // Exception 'com.mojang.brigadier.exceptions.CommandSyntaxException' is never thrown in the method
    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(new TranslationTextComponent("command.floormats.reset.feedback"), false);
        ServerWorld serverWorld = context.getSource().getServer().getWorld(World.field_234918_g_);
        ITextComponent feedback;
        if (serverWorld == null) {
            feedback = new TranslationTextComponent("command.floormats.reset.world_not_found");
        } else {
            feedback = new TranslationTextComponent("")
                    .append(new TranslationTextComponent("command.floormats.reset.corrections",Clusters.cmdReset(serverWorld)));
        }
        context.getSource().sendFeedback(feedback, false);
        return 0;
    }
}