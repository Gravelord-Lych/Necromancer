package lych.necromancer.capability.world.event;

import lych.necromancer.world.event.WorldEvent;
import lych.necromancer.world.event.context.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface WorldEventManager<T extends WorldEvent<C, ?>, C extends Context> extends INBTSerializable<CompoundTag> {
    T createEvent(BlockPos pos, C context);

    T get(int id);

    void tick();

    boolean has(int id);

    boolean has(T event);
}
