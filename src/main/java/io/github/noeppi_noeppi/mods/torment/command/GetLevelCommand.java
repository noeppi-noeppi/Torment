package io.github.noeppi_noeppi.mods.torment.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class GetLevelCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        EntitySelector sel = context.getArgument("players", EntitySelector.class);
        List<ServerPlayer> players = sel.findPlayers(context.getSource());
        if (players.isEmpty()) {
            context.getSource().sendFailure(new TranslatableComponent("command.torment.level.get.noplayers"));
        } else {
            for (ServerPlayer player : players) {
                TormentData data = TormentData.get(player);
                context.getSource().sendSuccess(new TranslatableComponent("command.torment.level.get.success", player.getDisplayName(), data.getTargetLevel()), false);
            }
        }
        return 0;
    }
}
