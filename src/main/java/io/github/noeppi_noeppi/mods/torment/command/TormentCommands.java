package io.github.noeppi_noeppi.mods.torment.command;

import net.minecraftforge.event.RegisterCommandsEvent;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.EntityArgument.players;

public class TormentCommands {
    
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(literal("torment").requires(p -> p.hasPermission(2))
                .then(literal("level")
                        .then(literal("get").then(argument("players", players()).executes(new GetLevelCommand())))
                        .then(literal("set").then(argument("players", players()).then(argument("level", integer(0, 20)).executes(new SetLevelCommand()))))
        ));
    }
}
