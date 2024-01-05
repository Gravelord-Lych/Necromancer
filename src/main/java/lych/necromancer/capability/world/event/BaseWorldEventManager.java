package lych.necromancer.capability.world.event;

import lych.necromancer.Necromancer;
import lych.necromancer.world.event.AbstractWorldEvent;
import lych.necromancer.world.event.WorldEvent;
import lych.necromancer.world.event.context.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

public abstract class BaseWorldEventManager<T extends WorldEvent<C, ?>, C extends Context> implements WorldEventManager<T, C> {
    private static final String NEXT_AVAILABLE_ID_TAG = "NextAvailableID";
    private static final String TICK_TAG = "Tick";
    private static final String ID_TAG = "ID";
    private static final String EVENT_TAG = "Event";
    private static final String EVENTS_TAG = "Events";

    protected final ServerLevel level;
    protected final Map<Integer, T> events = new HashMap<>();
    private int nextAvailableID;
    private int tick;

    protected BaseWorldEventManager(ServerLevel level) {
        this.level = level;
        nextAvailableID = 1;
    }

    @Override
    public T createEvent(BlockPos pos, C context) {
        T event = constructEvent(pos, nextAvailableID, context);
        add(event);
        infoEventAdded(event);
        return event;
    }

    protected final void add(T event) {
        events.put(nextAvailableID++, event);
    }

    protected void infoEventAdded(T event) {
        Necromancer.LOGGER.info(AbstractWorldEvent.EVENTS, "Added event {}, context = [{}]", event, event.getContext());
    }

    @Override
    public T get(int id) {
        return events.get(id);
    }

    @Override
    public void tick() {
        tick++;
        for (Iterator<T> itr = events.values().iterator(); itr.hasNext(); ) {
            T event = itr.next();
            if (event.isStopped()) {
                itr.remove();
            } else {
                event.tick();
            }
        }
    }

    @Override
    public boolean has(int id) {
        return events.containsKey(id);
    }

    @Override
    public boolean has(T event) {
        return has(event.getId());
    }

    protected abstract T constructEvent(BlockPos pos, int nextAvailableID, C context);

    protected abstract T loadEvent(CompoundTag tag);

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(NEXT_AVAILABLE_ID_TAG, nextAvailableID);
        tag.putInt(TICK_TAG, tick);
        ListTag listTag = new ListTag();
        for (T event : events.values()) {
            CompoundTag eventTag = new CompoundTag();
            eventTag.putInt(ID_TAG, event.getId());
            eventTag.put(EVENT_TAG, event.serializeNBT());
            listTag.add(eventTag);
        }
        tag.put(EVENTS_TAG, listTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains(NEXT_AVAILABLE_ID_TAG, CompoundTag.TAG_INT)) {
            nextAvailableID = nbt.getInt(NEXT_AVAILABLE_ID_TAG);
        }
        tick = nbt.getInt(TICK_TAG);
        if (nbt.contains(EVENTS_TAG, CompoundTag.TAG_LIST)) {
            ListTag listTag = nbt.getList(EVENTS_TAG, CompoundTag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag eventTag = listTag.getCompound(i);
                int id = eventTag.getInt(ID_TAG);
                T event = loadEvent(eventTag.getCompound(EVENT_TAG));
                events.put(id, event);
            }
        }
    }

    public static class Provider<T extends BaseWorldEventManager<?, ?>> implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final Capability<T> type;
        @Nullable
        private T manager;
        private final Supplier<? extends T> sup;

        public Provider(Capability<T> type, Supplier<? extends T> sup) {
            this.type = type;
            this.sup = sup;
        }

        @NotNull
        @Override
        public <T1> LazyOptional<T1> getCapability(@NotNull Capability<T1> cap, @Nullable Direction side) {
            return cap == type ? LazyOptional.of(this::get).cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return get().serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            get().deserializeNBT(nbt);
        }

        private T get() {
            if (manager == null) {
                manager = sup.get();
            }
            return manager;
        }
    }
}
