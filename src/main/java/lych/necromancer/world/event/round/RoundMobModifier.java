package lych.necromancer.world.event.round;

import lych.necromancer.util.AttributeModifierPair;
import lych.necromancer.world.event.AbstractOnslaught;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public record RoundMobModifier<T extends Mob, U extends AbstractOnslaught<U, ?>> (
        List<Function<? super U, ? extends AttributeModifierPair>> attributeModifiers,
        BiConsumer<? super T, ? super U> additionalModifier
) implements BiConsumer<T, U> {
    private static final RoundMobModifier<?, ?> EMPTY = new RoundMobModifier<>(List.of(), (onslaught, mob) -> {});

    @SuppressWarnings("unchecked")
    public static <T extends Mob, U extends AbstractOnslaught<U, ?>> RoundMobModifier<T, U> empty() {
        return (RoundMobModifier<T, U>) EMPTY;
    }

    public static <T extends Mob, U extends AbstractOnslaught<U, ?>> Builder<T, U> builder() {
        return new Builder<>();
    }

    public RoundMobModifier<T, U> plus(RoundMobModifier<? super T, ? super U> another) {
        return new RoundMobModifier<>(Stream.concat(attributeModifiers.stream(), another.attributeModifiers.stream()).distinct().toList(),
                mergeAdditionalModifier(another));
    }

    private BiConsumer<T, U> mergeAdditionalModifier(RoundMobModifier<? super T, ? super U> another) {
        return (mob, onslaught) -> {
            additionalModifier.accept(mob, onslaught);
            another.additionalModifier.accept(mob, onslaught);
        };
    }

    @Override
    public void accept(T mob, U onslaught) {
        attributeModifiers.forEach(modifier -> modifier.apply(onslaught).accept(mob));
        additionalModifier.accept(mob, onslaught);
    }

    public static class Builder<T extends Mob, U extends AbstractOnslaught<U, ?>> {
        private final List<Function<? super U, ? extends AttributeModifierPair>> attributeModifiers = new ArrayList<>();
        private BiConsumer<T, U> additionalModifier = (mob, onslaught) -> {};

        Builder() {}

        public Builder<T, U> modify(AttributeModifierPair modifier) {
            return modify(onslaught -> modifier);
        }

        public Builder<T, U> modify(Function<? super U, ? extends AttributeModifierPair> modifier) {
            attributeModifiers.add(modifier);
            return this;
        }

        public Builder<T, U> also(BiConsumer<? super T, ? super U> additionalModifier) {
            this.additionalModifier = this.additionalModifier.andThen(additionalModifier);
            return this;
        }

        public RoundMobModifier<T, U> build() {
            return new RoundMobModifier<>(attributeModifiers, additionalModifier);
        }
    }
}
