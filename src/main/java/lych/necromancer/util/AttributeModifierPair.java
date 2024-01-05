package lych.necromancer.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Objects;
import java.util.function.Consumer;

public record AttributeModifierPair(Attribute attribute, AttributeModifier modifier, boolean permanent) implements Consumer<LivingEntity> {
    public static AttributeModifierPair of(Attribute attribute, AttributeModifier modifier) {
        return new AttributeModifierPair(attribute, modifier, false);
    }

    public static AttributeModifierPair permanent(Attribute attribute, AttributeModifier modifier) {
        return new AttributeModifierPair(attribute, modifier, true);
    }

    @Override
    public void accept(LivingEntity entity) {
        AttributeInstance attr = Objects.requireNonNull(entity.getAttribute(attribute), "Attribute must not be null!");
        if (permanent) {
            attr.addPermanentModifier(modifier);
        } else {
            attr.addTransientModifier(modifier);
        }
    }
}
