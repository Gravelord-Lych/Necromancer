package lych.necromancer.capability.world.event;

import lych.necromancer.world.event.FirstStrike;
import lych.necromancer.world.event.context.SingleTargetContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class FirstStrikeManager extends BaseWorldEventManager<FirstStrike, SingleTargetContext> {
    public FirstStrikeManager(ServerLevel level) {
        super(level);
    }

    @Override
    protected FirstStrike constructEvent(BlockPos pos, int nextAvailableID, SingleTargetContext context) {
        return new FirstStrike(nextAvailableID, pos, level, context);
    }

    @Override
    protected FirstStrike loadEvent(CompoundTag tag) {
        return new FirstStrike(tag, level);
    }

    public boolean hasActiveFirstStrikeFor(ServerPlayer player) {
        return events.values().stream().anyMatch(event -> event.viewTargetUUIDs().contains(player.getUUID()));
    }
}
