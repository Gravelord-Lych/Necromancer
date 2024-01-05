package lych.necromancer.world.event.round;

import com.google.common.collect.ForwardingMap;
import lych.necromancer.world.event.AbstractOnslaught;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Rounds<T extends AbstractOnslaught<T, ?>> extends ForwardingMap<Integer, Round<T>> {
    private final Map<Integer, Round<T>> rounds;
    private int maxRound;

    private Rounds(Map<Integer, Round<T>> rounds) {
        this.rounds = Collections.unmodifiableMap(rounds);
        maxRound = Math.max(maxRound, rounds.keySet().stream().max(Integer::compareTo).orElse(0));
    }

    public static <T extends AbstractOnslaught<T, ?>> Builder<T> at(int round) {
        return new Builder<T>().at(round);
    }

    public boolean isFinalRound(int round) {
        return round == maxRound;
    }

    @Override
    protected Map<Integer, Round<T>> delegate() {
        return rounds;
    }

    public static class Builder<T extends AbstractOnslaught<T, ?>> {
        private final Map<Integer, Round<T>> rounds = new HashMap<>();
        private RoundMobModifier<?, ? super T> globalModifier = RoundMobModifier.empty();
        private int currentRound = -1;

        private Builder() {}

        public Builder<T> modifier(RoundMobModifier.Builder<?, ? super T> modifier) {
            return modifier(modifier.build());
        }

        public Builder<T> modifier(RoundMobModifier<?, ? super T> modifier) {
            this.globalModifier = modifier;
            return this;
        }

        public Builder<T> at(int round) {
            this.currentRound = round;
            return this;
        }

        public Builder<T> spawns(Round.Builder<T> builder) {
            if (builder.hasCustomModifier()) {
                return spawns(builder.build());
            }
            return spawns(builder.modifier(globalModifier).build());
        }

        private Builder<T> spawns(Round<T> round) {
            if (currentRound == -1) {
                throw new IllegalStateException("No round selected");
            }
            rounds.put(currentRound, round);
            currentRound = -1;
            return this;
        }

        public Rounds<T> createRounds() {
            return new Rounds<>(rounds);
        }
    }
}
