package lych.necromancer.world.level;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import net.minecraft.world.level.GameRules.Key;

import static net.minecraft.world.level.GameRules.register;

public final class ModGameRules {
    public static final Key<BooleanValue> NCMRULE_DISABLE_FIRST_STRIKES = register("ncmDisableFirstStrikes", Category.MOBS, bool(false));

    private ModGameRules() {}

    public static void init() {}

    public static GameRules.Type<BooleanValue> bool(boolean defaultValue) {
        return BooleanValue.create(defaultValue);
    }
}
