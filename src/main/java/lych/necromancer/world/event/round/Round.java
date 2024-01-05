package lych.necromancer.world.event.round;

import lych.necromancer.entity.monster.BaseNecromancyCreation;
import lych.necromancer.world.event.AbstractOnslaught;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Round<T extends AbstractOnslaught<T, ?>> {
    private final List<RoundMob<?, T>> mobs;
    private final Set<Hint<T>> hints;
    private final double heightRequirement;
    private final double rangeModifier;

    private Round(List<RoundMob<?, T>> mobs, Set<Hint<T>> hints, double rangeModifier) {
        this.mobs = mobs;
        this.heightRequirement = mobs.stream().map(RoundMob::type).mapToDouble(EntityType::getHeight).max().orElse(1);
        this.hints = hints;
        this.rangeModifier = rangeModifier;
    }

    @Nullable
    public Hint<T> getHintAt(int time) {
        return hints.stream().filter(hint -> hint.time() == time).findFirst().orElse(null);
    }

    public void spawnAll(T onslaught) {
        for (RoundMob<?, T> rm : mobs) {
            rm.spawnAll(onslaught, this);
        }
    }

    public double getHeightRequirement() {
        return heightRequirement;
    }

    public double getRangeModifier() {
        return rangeModifier;
    }

    public static <T extends AbstractOnslaught<T, ?>> Builder<T> round() {
        return new Builder<>();
    }

    public static final class Builder<T extends AbstractOnslaught<T, ?>> {
        private final List<RoundMob<?, T>> mobs = new ArrayList<>();
        private final Set<Hint<T>> hints = new HashSet<>();
        private RoundMobModifier<?, ? super T> generalModifier = RoundMobModifier.empty();
        private double rangeModifier = 1;
        private boolean hasCustomModifier;

        private Builder() {}

        public <M extends BaseNecromancyCreation> Builder<T> addMob(RoundMob<M, T> mob) {
            if (mob.hasCustomModifier()) {
                mobs.add(mob);
                return this;
            }
            mobs.add(forceSetGeneralModifier(mob));
            return this;
        }

        @SuppressWarnings("unchecked")
        private <M extends BaseNecromancyCreation> RoundMob<M, T> forceSetGeneralModifier(RoundMob<M, T> mob) {
            try {
                return mob.setModifier((RoundMobModifier<? super M, ? super T>) generalModifier);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("General modifier is not compatible with entity type %s".formatted(mob.type().toShortString()), e);
            }
        }

        public Builder<T> addHint(int time, Component message) {
            return addHint(time, new HintProvider<>(message.getString(), (event, player) -> message));
        }

        public Builder<T> addHint(int time, HintProvider<T> message) {
            hints.add(new Hint<>(time, message));
            return this;
        }

        public Builder<T> rangeModifier(double rangeModifier) {
            this.rangeModifier = rangeModifier;
            return this;
        }

        public Builder<T> modifier(RoundMobModifier<?, ? super T> modifier) {
            this.generalModifier = modifier;
            reloadMobs();
            hasCustomModifier = true;
            return this;
        }

        private void reloadMobs() {
            if (mobs.isEmpty()) {
                return;
            }
            List<? extends RoundMob<?, T>> newMobs = mobs.stream().map(this::forceSetGeneralModifier).toList();
            mobs.clear();
            newMobs.forEach(this::addMob);
        }

        boolean hasCustomModifier() {
            return hasCustomModifier;
        }

        public Round<T> build() {
            return new Round<>(List.copyOf(mobs), Set.copyOf(hints), rangeModifier);
        }
    }
}
