package lych.necromancer.capability.world.event;

import lych.necromancer.world.event.Onslaught;
import lych.necromancer.world.event.context.MutableTargetContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public final class OnslaughtManager extends BaseWorldEventManager<Onslaught, MutableTargetContext> {
    public OnslaughtManager(ServerLevel level) {
        super(level);
    }

    @Override
    protected Onslaught constructEvent(BlockPos pos, int nextAvailableID, MutableTargetContext context) {
        return new Onslaught(nextAvailableID, pos, level, context);
    }

    @Override
    protected Onslaught loadEvent(CompoundTag tag) {
        return new Onslaught(tag, level);
    }
}
