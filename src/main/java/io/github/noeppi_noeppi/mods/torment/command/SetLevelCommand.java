package io.github.noeppi_noeppi.mods.torment.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.List;

public class SetLevelCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        EntitySelector sel = context.getArgument("players", EntitySelector.class);
        int level = Mth.clamp(context.getArgument("level", Integer.class), 0,20);
        List<ServerPlayer> players = sel.findPlayers(context.getSource());
        for (ServerPlayer player : players) {
            TormentData data = TormentData.get(player);
            data.forceTargetLevel(level);
        }
        context.getSource().sendSuccess(new TranslatableComponent("command.torment.level.set.success", players.size()), false);
        return 0;
    }
}
