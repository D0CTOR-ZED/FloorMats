package zed.d0c.floormats.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import zed.d0c.floormats.FloorMats;

public class ModCommands {

    // Variable 'cmd' is never used
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> cmd = dispatcher.register(
                Commands.literal(FloorMats.MODID)
                        .executes(Command_None.register(dispatcher).getCommand())
                        .then(Command_Help_Topic.register(dispatcher))
                        .then(Command_Reset.register(dispatcher))
                        .then(Command_Tools.register(dispatcher))
        );

        // Don't see any reason to alias the command, /floormats is fine.
        // dispatcher.register(Commands.literal("mats").redirect(cmd));
    }

}
