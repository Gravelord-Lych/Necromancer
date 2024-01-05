package lych.necromancer.world.event.context;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class MutableTargetContext implements TargetContext {
    public static final String TARGETS_TAG = "Targets";
    private final Set<UUID> targets = new HashSet<>();

    public MutableTargetContext(Collection<UUID> targets) {
        this.targets.addAll(targets);
    }

    public MutableTargetContext(CompoundTag tag) {
        if (tag.contains(TARGETS_TAG, CompoundTag.TAG_LIST)) {
            targets.clear();
            ListTag targetsTag = tag.getList(TARGETS_TAG, CompoundTag.TAG_INT_ARRAY);
            for (Tag uuid : targetsTag) {
                targets.add(NbtUtils.loadUUID(uuid));
            }
        }
    }

    @Override
    public boolean canAddTarget() {
        return true;
    }

    @Override
    public boolean addTarget(UUID uuid) {
        return targets.add(uuid);
    }

    @Override
    public Set<UUID> viewTargetUUIDs() {
        return Set.copyOf(targets);
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag targetsTag = new ListTag();
        for (UUID uuid : targets) {
            targetsTag.add(NbtUtils.createUUID(uuid));
        }
        tag.put(TARGETS_TAG, targetsTag);
        return tag;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("targets", targets)
                .toString();
    }
}
