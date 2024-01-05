package lych.necromancer.world.event;

import lych.necromancer.world.event.context.SingleTargetContext;
import lych.necromancer.world.event.context.TargetContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface TargetedWorldEvent<C extends TargetContext, M extends Mob> extends WorldEvent<C, M> {
    void updateTarget(UUID newTarget);

    default Set<UUID> viewTargetUUIDs() {
        return getContext().viewTargetUUIDs();
    }

    default boolean addTarget(UUID uuid) {
        if (!getContext().canAddTarget()) {
            throw new UnsupportedOperationException("Adding target is not supported");
        }
        updateTarget(uuid);
        return getContext().addTarget(uuid);
    }

    default boolean isTarget(UUID uuid) {
        return getContext().isTarget(uuid);
    }

    default Set<ServerPlayer> viewTargets() {
        return viewTargetUUIDs().stream().map(uuid -> level().getPlayerByUUID(uuid) instanceof ServerPlayer p ? p : null).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    default boolean hasSingleTarget() {
        return getContext() instanceof SingleTargetContext;
    }

    @Nullable
    default Player getSingleTarget() {
        if (hasSingleTarget()) {
            if (viewTargetUUIDs().size() != 1) {
                throw new IllegalStateException("More than 1 target found");
            }
            return level().getPlayerByUUID(viewTargetUUIDs().iterator().next());
        }
        throw new UnsupportedOperationException("This event does not have a single target");
    }

    default boolean addTarget(Player player) {
        return addTarget(player.getUUID());
    }

    default boolean isTarget(Player player) {
        return isTarget(player.getUUID());
    }
}
