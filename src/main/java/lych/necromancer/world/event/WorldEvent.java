package lych.necromancer.world.event;

import lych.necromancer.Necromancer;
import lych.necromancer.world.event.context.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.INBTSerializable;

public interface WorldEvent<C extends Context, M extends Mob> extends INBTSerializable<CompoundTag> {
    String EVENT = "event";

    int getId();

    BlockPos getCenter();

    void tick();

    Status getStatus();

    void setStatus(Status status);

    void stop();

    boolean isActive();

    ServerLevel level();

    C getContext();

    void onMobAdded(M mob, boolean updateBossBar);

    default boolean isFinished() {
        return getStatus() != Status.ONGOING;
    }

    default boolean isVictory() {
        return getStatus() == Status.VICTORY;
    }

    default boolean isStopped() {
        return getStatus() == Status.STOPPED;
    }

    enum Status {
        ONGOING,
        VICTORY,
        LOSS,
        STOPPED;

        public String getName() {
            return name().toLowerCase();
        }

        public Component getTranslationKey() {
            return Component.translatable(Necromancer.prefixMsg(EVENT, "status." + getName()));
        }
    }
}
