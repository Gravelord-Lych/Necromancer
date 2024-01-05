package lych.necromancer.capability.world.event;

import lych.necromancer.world.level.ModGameRules;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public interface IFirstStrikeDataStorage extends INBTSerializable<CompoundTag> {
    boolean hasStruck(UUID playerUUID);

    default boolean setStruck(UUID playerUUID) {
        return setStruck(playerUUID, true);
    }

    boolean setStruck(UUID playerUUID, boolean struck);

    boolean enableFirstStrikes();

    void tick();

    boolean tryToStartFirstStrikeFor(ServerPlayer player, boolean considerTime);

    default boolean hasStruck(Player player) {
        return hasStruck(player.getUUID());
    }

    static boolean enableFirstStrikes(Level level) {
        return !level.getGameRules().getBoolean(ModGameRules.NCMRULE_DISABLE_FIRST_STRIKES);
    }
}
