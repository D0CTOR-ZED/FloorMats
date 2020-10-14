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
                        .then(CommandReset.register(dispatcher))
        );

        // Don't see any reason to alias the command, /floormats is fine.
        // dispatcher.register(Commands.literal("mats").redirect(cmd));
    }

}
