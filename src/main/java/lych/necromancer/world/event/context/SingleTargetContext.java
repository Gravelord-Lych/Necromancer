package lych.necromancer.world.event.context;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.CompoundTag;

import java.util.Set;
import java.util.UUID;

public final class SingleTargetContext implements TargetContext {
    public static final String TARGET_TAG = "Target";
    private final UUID target;

    public SingleTargetContext(UUID target) {
        this.target = target;
    }

    public SingleTargetContext(CompoundTag tag) {
        this.target = tag.getUUID(TARGET_TAG);
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID(TARGET_TAG, target);
        return tag;
    }

    @Override
    public boolean canAddTarget() {
        return false;
    }

    @Override
    public boolean addTarget(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<UUID> viewTargetUUIDs() {
        return Set.of(target);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("target", target)
                .toString();
    }
}
