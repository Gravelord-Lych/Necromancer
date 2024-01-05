package lych.necromancer.world.event;

import lych.necromancer.Necromancer;
import lych.necromancer.world.event.context.Context;
import lych.necromancer.world.event.context.TargetContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Mob;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public abstract class AbstractWorldEvent<C extends Context, M extends Mob> implements WorldEvent<C, M> {
    public static final Marker EVENTS = MarkerFactory.getMarker("WorldEvents");
    private static final String TAG_ID = "Id";
    private static final String TAG_CENTER = "Center";
    private static final String TAG_STATUS = "Status";
    private static final String TAG_POST_EVENT_TICKS = "PostEventTicks";
    private static final String TAG_TICKS_ACTIVE = "TicksActive";
    private static final String TAG_ACTIVE = "Active";
    private static final String TAG_CONTEXT = "Context";

    protected final ServerBossEvent event;
    private final int id;
    private final BlockPos center;
    private final ServerLevel level;
    private final C context;
    private Status status = Status.ONGOING;
    private int postEventTicks;
    private int ticksActive;
    private boolean active;

    protected AbstractWorldEvent(int id, BlockPos center, ServerLevel level, C context) {
        this.id = id;
        this.center = center;
        this.level = level;
        this.context = context;
        this.event = initBossEvent();
        event.setProgress(0);
        updateTargetFromContext(context);
    }

    protected AbstractWorldEvent(CompoundTag tag, ServerLevel level) {
        id = tag.getInt(TAG_ID);
        center = NbtUtils.readBlockPos(tag.getCompound(TAG_CENTER));
        context = loadContext(tag.getCompound(TAG_CONTEXT));
        this.level = level;
        deserializeNBT(tag);
        this.event = initBossEvent();
        event.setProgress(0);
        updateTargetFromContext(context);
    }

    protected abstract ServerBossEvent initBossEvent();

    private void updateTargetFromContext(Context context) {
        if (this instanceof TargetedWorldEvent<?, ?> t) {
            if (context instanceof TargetContext c) {
                c.viewTargetUUIDs().forEach(t::updateTarget);
            } else {
                throw new IllegalStateException("TargetedWorldEvent do not have a TargetContext");
            }
        }
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(final Status status) {
        this.status = status;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick() {
        updateTargetFromContext(context);
        if (!isStopped()) {
            active = level.hasChunkAt(center);
            if (isFinished()) {
                postEventTicks++;
                postEventTick();

                if (postEventTicks >= getMaxPostEventTicks()) {
                    stop();
                    return;
                }
            }

            if (shouldStopIfPeaceful() && level.getDifficulty() == Difficulty.PEACEFUL) {
                stop();
                return;
            }

            if (isActive()) {
                ticksActive++;
                eventTick();
            }
        }
    }

    protected abstract void eventTick();

    protected abstract void postEventTick();

    protected boolean shouldStopIfPeaceful() {
        return true;
    }

    protected int getMaxPostEventTicks() {
        return 600;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public BlockPos getCenter() {
        return center;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void stop() {
        status = Status.STOPPED;
        event.removeAllPlayers();
    }

    @Override
    public ServerLevel level() {
        return level;
    }

    protected int ticksActive() {
        return ticksActive;
    }

    protected static Component makeComponent(String text) {
        return makeComponent(text, TranslatableContents.NO_ARGS);
    }

    protected static Component makeComponent(String text, Object... args) {
        return Component.translatable(Necromancer.prefixMsg(EVENT, text), args);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return obj.getClass() == getClass() && ((WorldEvent<?, ?>) obj).getId() == getId();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public C getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "[%s#%d at %s in %s]".formatted(getClass().getSimpleName(), id, center, level.dimension().location());
    }

    @Override
    public final CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(TAG_ID, id);
        nbt.put(TAG_CENTER, NbtUtils.writeBlockPos(center));
        nbt.putString(TAG_STATUS, status.name());
        nbt.putInt(TAG_POST_EVENT_TICKS, postEventTicks);
        nbt.putInt(TAG_TICKS_ACTIVE, ticksActive);
        nbt.putBoolean(TAG_ACTIVE, active);
        nbt.put(TAG_CONTEXT, context.save());
        addAdditionalSaveData(nbt);
        return nbt;
    }

    protected abstract void addAdditionalSaveData(CompoundTag nbt);

    protected abstract C loadContext(CompoundTag nbt);

    @Override
    public final void deserializeNBT(CompoundTag nbt) {
        status = Status.valueOf(nbt.getString(TAG_STATUS));
        postEventTicks = nbt.getInt(TAG_POST_EVENT_TICKS);
        ticksActive = nbt.getInt(TAG_TICKS_ACTIVE);
        active = nbt.getBoolean(TAG_ACTIVE);
        readAdditionalSaveData(nbt);
    }

    protected abstract void readAdditionalSaveData(CompoundTag nbt);
}
