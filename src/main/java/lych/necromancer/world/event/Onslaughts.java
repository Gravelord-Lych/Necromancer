package lych.necromancer.world.event;

import lych.necromancer.capability.ModCapabilities;
import lych.necromancer.capability.world.event.WorldEventManager;
import lych.necromancer.world.event.context.Context;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

public final class Onslaughts {
    private Onslaughts() {}

    @Nullable
    public static AbstractOnslaught<?, ?> getOnslaught(ServerLevel level, int id) {
        Onslaught onslaught = getOnslaught(ModCapabilities.ONSLAUGHT_MANAGER, level, id);
        FirstStrike firstStrike = getOnslaught(ModCapabilities.FIRST_STRIKE_MANAGER, level, id);
        return onslaught == null ? firstStrike : onslaught;
    }

    @Nullable
    public static <O extends WorldEvent<C, ?>, T extends WorldEventManager<O, C>, C extends Context> O getOnslaught(Capability<T> managerType, ServerLevel level, int id) {
        return level.getCapability(managerType).resolve().map(manager -> manager.get(id)).orElse(null);
    }
}
