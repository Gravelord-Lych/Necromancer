package lych.necromancer.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lych.necromancer.Necromancer;
import lych.necromancer.capability.ModCapabilities;
import lych.necromancer.capability.world.event.IFirstStrikeDataStorage;
import lych.necromancer.world.event.AbstractWorldEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class FirstStrikeCommand {
    public static final String COMMAND_NAME = "fs";
    public static final String SUCCESSFULLY_STRUCK = "successfully_struck";
    public static final String SUCCESSFULLY_REFRESH = "successfully_refresh";
    public static final String ALREADY_STRUCK_ID = "already_struck";
    public static final String NOT_STRUCK_ID = "not_struck";
    public static final SimpleCommandExceptionType NO_DATA_STORAGE = new SimpleCommandExceptionType(prefix("not_found"));
    public static final SimpleCommandExceptionType UNKNOWN_ERROR = new SimpleCommandExceptionType(prefix("error"));
    public static final DynamicCommandExceptionType ALREADY_STRUCK = new DynamicCommandExceptionType(name -> prefix(ALREADY_STRUCK_ID, name));
    public static final DynamicCommandExceptionType NOT_STRUCK = new DynamicCommandExceptionType(name -> prefix(NOT_STRUCK_ID, name));
    private static final String PLAYER = "player";

    private FirstStrikeCommand() {}

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return literal(COMMAND_NAME)
                .requires(source -> source.hasPermission(2))
                .then(literal("start")
                        .then(argument(PLAYER, EntityArgument.player())
                                .executes(FirstStrikeCommand::strike)))
                .then(literal("reset")
                        .then(argument(PLAYER, EntityArgument.player())
                                .executes(FirstStrikeCommand::reset)));
    }

    private static int strike(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        EntitySelector selector = ctx.getArgument(PLAYER, EntitySelector.class);
        ServerPlayer player = selector.findSinglePlayer(ctx.getSource());
        ServerLevel level = ctx.getSource().getLevel();
        IFirstStrikeDataStorage storage = level.getCapability(ModCapabilities.FIRST_STRIKE_DATA_STORAGE).resolve().orElse(null);
        if (storage == null) {
            throw NO_DATA_STORAGE.create();
        } else if (storage.hasStruck(player)) {
            throw ALREADY_STRUCK.create(player.getDisplayName());
        }
        if (storage.tryToStartFirstStrikeFor(player, false)) {
            ctx.getSource().sendSuccess(() -> prefix(SUCCESSFULLY_STRUCK, player.getDisplayName()), true);
            return Command.SINGLE_SUCCESS;
        }
        Necromancer.LOGGER.warn(AbstractWorldEvent.EVENTS, "Failed to create FirstStrike for player {}", player.getDisplayName().getString());
        throw UNKNOWN_ERROR.create();
    }

    private static int reset(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        EntitySelector selector = ctx.getArgument(PLAYER, EntitySelector.class);
        ServerPlayer player = selector.findSinglePlayer(ctx.getSource());
        ServerLevel level = ctx.getSource().getLevel();
        IFirstStrikeDataStorage storage = level.getCapability(ModCapabilities.FIRST_STRIKE_DATA_STORAGE).resolve().orElse(null);
        if (storage == null) {
            throw NO_DATA_STORAGE.create();
        }
        if (storage.setStruck(player.getUUID(), false)) {
            ctx.getSource().sendSuccess(() -> prefix(SUCCESSFULLY_REFRESH, player.getDisplayName()), true);
            return Command.SINGLE_SUCCESS;
        }
        throw NOT_STRUCK.create(player.getDisplayName());
    }

    private static Component prefix(String name, Object... args) {
        return Component.translatable(Necromancer.prefixCmd(COMMAND_NAME, name), args);
    }
}
