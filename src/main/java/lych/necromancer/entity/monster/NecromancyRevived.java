package lych.necromancer.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract non-sealed class NecromancyRevived extends BaseNecromancyCreation {
    protected static final int DEFAULT_XP = 20;

    protected NecromancyRevived(EntityType<? extends NecromancyRevived> type, Level level) {
        super(type, level);
        xpReward = DEFAULT_XP;
    }
}
