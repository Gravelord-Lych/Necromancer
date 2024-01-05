package lych.necromancer.world.event;

import lych.necromancer.world.event.context.MutableTargetContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public final class Onslaught extends AbstractOnslaught<Onslaught, MutableTargetContext> {
    public Onslaught(int id, BlockPos center, ServerLevel level, MutableTargetContext context) {
        super(id, center, level, null, context);
    }

    public Onslaught(CompoundTag tag, ServerLevel level) {
        super(tag, null, level);
    }

    @Override
    protected Component getBossEventName() {
        return null;
    }

    @Override
    protected SpawnPositionProvider createPositionProvider() {
        return new SpawnPositionProvider(this, 15, 30);
    }

    @Override
    protected void eventTick() {

    }

    @Override
    protected void postEventTick() {

    }

    @Override
    protected MutableTargetContext loadContext(CompoundTag nbt) {
        return new MutableTargetContext(nbt);
    }
}
