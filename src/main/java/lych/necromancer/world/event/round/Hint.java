package lych.necromancer.world.event.round;

import lych.necromancer.world.event.AbstractWorldEvent;

public record Hint<T extends AbstractWorldEvent<?, ?>>(int time, HintProvider<T> message) implements Comparable<Hint<?>> {
    public static final String NAME = "hint";

    @Override
    public int compareTo(Hint<?> o) {
        return Integer.compare(time, o.time);
    }
}
