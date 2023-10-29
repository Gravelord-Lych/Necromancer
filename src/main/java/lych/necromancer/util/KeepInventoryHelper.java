package lych.necromancer.util;

import lych.necromancer.Necromancer;
import lych.necromancer.capability.ModCapabilities;
import lych.necromancer.capability.player.INecromancerData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.IntFunction;

@Mod.EventBusSubscriber(modid = Necromancer.MODID)
public final class KeepInventoryHelper {
    public static final int DEFAULT_KEEP_INVENTORY_TIMES = 3;
    public static final int MAX_KEEP_INVENTORY_TIMES = 10;
    public static final int INFINITE_KEEP_INVENTORY_TIMES = -10;

    public static final String MESSAGE_ID = Necromancer.prefixMsg("keep_inventory_time");

    private KeepInventoryHelper() {}

    public static boolean effectiveOn(Player player) {
        if (player instanceof ServerPlayer sp) {
            PlayerState state = findState(sp);
            return state.isEffective();
        }
        return false;
    }

    @SubscribeEvent
    public static void onLivingDropExperience(LivingExperienceDropEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(ModCapabilities.NECROMANCER_DATA).ifPresent(data -> {
                if (KeepInventoryHelper.effectiveOn(player)) {
                    event.setCanceled(true);
                }
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW) // Executes later than data-retrieving methods
    public static void onPlayerRespawn(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        newPlayer.getCapability(ModCapabilities.NECROMANCER_DATA).ifPresent(data -> {
            if (oldPlayer instanceof ServerPlayer oldServerPlayer && newPlayer instanceof ServerPlayer newServerPlayer) {
                oldServerPlayer.reviveCaps();
                PlayerState state = findState(oldServerPlayer);
                oldServerPlayer.invalidateCaps();

                if (state.isEffective()) {
                    newServerPlayer.getInventory().replaceWith(oldServerPlayer.getInventory());
                    newServerPlayer.experienceLevel = oldServerPlayer.experienceLevel;
                    newServerPlayer.totalExperience = oldServerPlayer.totalExperience;
                    newServerPlayer.experienceProgress = oldServerPlayer.experienceProgress;
                    newServerPlayer.setScore(oldServerPlayer.getScore());
                    if (state.hasCost()) {
                        data.setKeepInventoryTimes(data.getKeepInventoryTimes() - 1);
                    }
                }
                sendMessageTo(newServerPlayer, state);
            }
        });
    }

    public static void sendMessageTo(ServerPlayer player, PlayerState state) {
        player.sendSystemMessage(createMessage(player, state), true);
    }

    private static Component createMessage(ServerPlayer player, PlayerState state) {
        return state.getMessage(player.getCapability(ModCapabilities.NECROMANCER_DATA).map(INecromancerData::getKeepInventoryTimes).orElseThrow());
    }

    public static PlayerState findState(ServerPlayer player) {
        if (player.serverLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return PlayerState.RULE_ENABLED;
        }
        int times = player.getCapability(ModCapabilities.NECROMANCER_DATA).map(INecromancerData::getKeepInventoryTimes).orElse(0);
        if (times == INFINITE_KEEP_INVENTORY_TIMES) {
            return PlayerState.INFINITY;
        }
        if (times > 0) {
            return PlayerState.READY;
        } else {
            return PlayerState.DEPLETED;
        }
    }

    public static void addKeepInventoryTimes(Player player, int amount) {
        player.getCapability(ModCapabilities.NECROMANCER_DATA).ifPresent(data -> {
            int keepInventoryTimes = data.getKeepInventoryTimes();
            data.setKeepInventoryTimes(Mth.clamp(keepInventoryTimes + amount, 0, MAX_KEEP_INVENTORY_TIMES));
        });
    }

    public enum PlayerState {
        READY(true, true, Style.EMPTY, PlayerState::getColoredNumber),
        INFINITY(false, true, Style.EMPTY, PlayerState::getColoredInfinity),
        DEPLETED(false, false, Style.EMPTY.withColor(ChatFormatting.RED)),
        RULE_ENABLED(false, false, Style.EMPTY);

        private final boolean hasCost;
        private final boolean effective;
        private final boolean overridesMessage;
        private final Style messageStyle;
        private final IntFunction<Style> numberStyle;

        PlayerState(boolean hasCost, boolean effective, Style messageStyle) {
            this(hasCost, effective, true, messageStyle, i -> Style.EMPTY);
        }

        PlayerState(boolean hasCost, boolean effective,  Style messageStyle, IntFunction<Style> numberStyle) {
            this(hasCost, effective, false, messageStyle, numberStyle);
        }

        PlayerState(boolean hasCost, boolean effective, boolean overridesMessage, Style messageStyle, IntFunction<Style> numberStyle) {
            this.hasCost = hasCost;
            this.effective = effective;
            this.overridesMessage = overridesMessage;
            this.messageStyle = messageStyle;
            this.numberStyle = numberStyle;
        }

        private static Style getColoredNumber(int times) {
            if (times > 2) {
                return Style.EMPTY.withColor(ChatFormatting.GREEN);
            }
            return Style.EMPTY.withColor(times == 0 ? ChatFormatting.RED : ChatFormatting.GOLD);
        }

        private static Style getColoredInfinity(int times) {
            return Style.EMPTY.withColor(ChatFormatting.AQUA);
        }

        public boolean hasCost() {
            return hasCost;
        }

        public boolean isEffective() {
            return effective;
        }

        public String getMessageId() {
            return MESSAGE_ID + ".player_state." + name().toLowerCase();
        }

        public Component getMessage(int times) {
            if (overridesMessage) {
                return Component.translatable(getMessageId()).withStyle(getMessageStyle());
            }
            return Component.translatable(MESSAGE_ID,
                    Component.translatable(getMessageId(), times).withStyle(getNumberStyle(times))).withStyle(getMessageStyle());
        }

        public Style getMessageStyle() {
            return messageStyle;
        }

        public Style getNumberStyle(int times) {
            return numberStyle.apply(times);
        }
    }
}
