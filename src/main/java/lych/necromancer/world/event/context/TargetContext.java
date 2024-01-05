package lych.necromancer.world.event.context;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public sealed interface TargetContext extends Context permits SingleTargetContext, MutableTargetContext {
    boolean canAddTarget();

    boolean addTarget(UUID uuid);

    default void addTarget(LivingEntity target) {
        addTarget(target.getUUID());
    }

    Set<UUID> viewTargetUUIDs();

    default boolean isTarget(UUID uuid) {
        return viewTargetUUIDs().contains(uuid);
    }

    default boolean isTarget(@Nullable LivingEntity entity) {
        return entity != null && isTarget(entity.getUUID());
    }
}
