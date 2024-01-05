package lych.necromancer.world.event.round;

import com.google.common.base.MoreObjects;
import lych.necromancer.entity.monster.BaseNecromancyCreation;
import lych.necromancer.entity.monster.NecromancyGenerated;
import lych.necromancer.world.event.AbstractOnslaught;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Objects;
import java.util.function.Function;

public final class RoundMob<T extends BaseNecromancyCreation, U extends AbstractOnslaught<U, ?>> {
    private final EntityType<T> type;
    private final Function<? super AbstractOnslaught<?, ?>, ? extends SpawnGroupData> data;
    private final int count;
    private final RoundMobModifier<? super T, ? super U> modifier;
    private final boolean hasCustomModifier;

    private RoundMob(EntityType<T> type, int count) {
        this(type, onslaught -> null, count);
    }

    private RoundMob(EntityType<T> type, Function<? super AbstractOnslaught<?, ?>, ? extends SpawnGroupData> data, int count) {
        this(type, data, count, RoundMobModifier.empty(), false);
    }

    private RoundMob(EntityType<T> type, Function<? super AbstractOnslaught<?, ?>, ? extends SpawnGroupData> data, int count, RoundMobModifier<? super T, ? super U> modifier) {
        this(type, data, count, modifier, true);
    }

    private RoundMob(EntityType<T> type,
                     Function<? super AbstractOnslaught<?, ?>, ? extends SpawnGroupData> data,
                     int count,
                     RoundMobModifier<? super T, ? super U> modifier,
                     boolean hasCustomModifier) {
        this.type = type;
        this.data = data;
        this.count = count;
        this.modifier = modifier;
        this.hasCustomModifier = hasCustomModifier;
    }

    public static <T extends BaseNecromancyCreation, U extends AbstractOnslaught<U, ?>> RoundMob<T, U> of(EntityType<T> type, Function<? super AbstractOnslaught<?, ?>, ? extends SpawnGroupData> data, int count) {
        return new RoundMob<>(type, data, count);
    }

    public static <T extends BaseNecromancyCreation, U extends AbstractOnslaught<U, ?>> RoundMob<T, U> of(EntityType<T> type, int count) {
        return new RoundMob<>(type, count);
    }

    @SuppressWarnings("Convert2Diamond") // Type cannot be inferred here
    public static <T extends BaseNecromancyCreation, U extends AbstractOnslaught<U, ?>> RoundMob<T, U> of(EntityType<T> type,
                                                                                                          Function<? super AbstractOnslaught<?, ?>, ? extends SpawnGroupData> data,
                                                                                                          int count,
                                                                                                          RoundMobModifier<? super T, ? super U> modifier) {
        return new RoundMob<T, U>(type, data, count, modifier);
    }

    @SuppressWarnings("RedundantTypeArguments") // Type cannot be inferred here
    public RoundMob<T, U> setModifier(RoundMobModifier<? super T, ? super U> modifier) {
        return RoundMob.<T, U>of(type, data, count, modifier);
    }

    @SuppressWarnings("unchecked") // U is a recursive type parameter
    public void spawnAll(AbstractOnslaught<U, ?> onslaught, Round<U> round) {
        ServerLevel level = onslaught.level();
        for (int i = 0; i < count; i++) {
            T mob = type.create(level);
            if (mob != null) {
                Vec3 pos = onslaught.positionProvider().getMobPosition(round);
                mob.moveTo(pos.x, pos.y, pos.z, level.random.nextFloat() * 360F, 0);
                mob.join(onslaught);
                modifier.accept(mob, (U) onslaught);
                ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(BlockPos.containing(pos)), MobSpawnType.EVENT, data.apply(onslaught), null);
                level.addFreshEntityWithPassengers(mob);
            }
        }
    }

    boolean hasCustomModifier() {
        return hasCustomModifier;
    }

    public static Function<? super AbstractOnslaught<?, ?>, ? extends SpawnGroupData> empty() {
        return onslaught -> NecromancyGenerated.empty();
    }

    public static Function<? super AbstractOnslaught<?, ?>, ? extends SpawnGroupData> noProperties() {
        return onslaught -> NecromancyGenerated.specifySize(NecromancyGenerated.Size.random(onslaught.level().getRandom()));
    }

    public EntityType<T> type() {
        return type;
    }

    public Function<? super AbstractOnslaught<?, ?>, ? extends SpawnGroupData> data() {
        return data;
    }

    public int count() {
        return count;
    }

    public RoundMobModifier<? super T, ? super U> modifier() {
        return modifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RoundMob<?, ?>) obj;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.data, that.data) &&
                this.count == that.count &&
                Objects.equals(this.modifier, that.modifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, data, count, modifier);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("data", data)
                .add("count", count)
                .add("modifier", modifier)
                .toString();
    }
}
