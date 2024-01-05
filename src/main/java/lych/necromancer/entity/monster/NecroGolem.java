package lych.necromancer.entity.monster;

import lych.necromancer.sound.ModSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class NecroGolem extends NecromancyGenerated {
    private static final double SPEED = 0.265;
    private static final Map<Difficulty, Double> EFFECT_PROBABILITY_MAP =
            Map.of(Difficulty.EASY, 0.25, Difficulty.NORMAL, 0.5, Difficulty.HARD, 1.0);
    private static final double MIN_EFFECT_DURATION_MUL = 0.5;
    private static final double MAX_EFFECT_DURATION_MUL = 1.5;
    private static final float REGENERATE_HEALTH_OF_SELF_PERCENT_WHILE_ATTACKING = 5F;
    private static final float REGENERATE_HEALTH_OF_NEARBY_PERCENT_WHILE_ATTACKING = 2F;
    private static final List<WeightedEntry.Wrapper<MobEffectInstance>> POSSIBLE_EFFECTS = List.of(
            WeightedEntry.wrap(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1), 3),
            WeightedEntry.wrap(new MobEffectInstance(MobEffects.HUNGER, 200, 1), 2),
            WeightedEntry.wrap(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0), 2),
            WeightedEntry.wrap(new MobEffectInstance(MobEffects.POISON, 200, 0), 1),
            WeightedEntry.wrap(new MobEffectInstance(MobEffects.WITHER, 200, 0), 1)
    );

    public NecroGolem(EntityType<? extends NecroGolem> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createNecromancyGeneratedMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, SPEED);
    }

    @Override
    protected void addBehaviorGoals() {
        super.addBehaviorGoals();
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1, false));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt) {
            if (hasProperty(Property.REGENERATION)) {
                tryHealNearby();
            }
            if (hasProperty(Property.INSTABILITY) && target instanceof LivingEntity livingTarget) {
                tryAffectTarget(livingTarget);
            }
        }
        return hurt;
    }

    private void tryHealNearby() {
        heal(getMaxHealth() * (REGENERATE_HEALTH_OF_SELF_PERCENT_WHILE_ATTACKING / 100F));
        addHeartParticles(random.nextIntBetweenInclusive(2, 3));
        for (BaseNecromancyCreation mob : getHealableEntities()) {
            mob.heal(mob.getMaxHealth() * (REGENERATE_HEALTH_OF_NEARBY_PERCENT_WHILE_ATTACKING / 100F));
            addHeartParticles(mob, random.nextIntBetweenInclusive(1, 2));
        }
    }

    private void tryAffectTarget(LivingEntity target) {
        if (random.nextDouble() < EFFECT_PROBABILITY_MAP.get(level().getDifficulty())) {
            WeightedEntry.Wrapper<MobEffectInstance> effectWrapper = WeightedRandom.getRandomItem(random, POSSIBLE_EFFECTS).orElseThrow();
            MobEffectInstance effect = effectWrapper.getData();
            double averageDurationMul = (MIN_EFFECT_DURATION_MUL + MAX_EFFECT_DURATION_MUL) / 2;
            target.addEffect(new MobEffectInstance(effect.getEffect(),
                    (int) (effect.getDuration() * random.triangle(averageDurationMul, MAX_EFFECT_DURATION_MUL - MIN_EFFECT_DURATION_MUL)),
                    effect.getAmplifier()));
        }
    }

    private List<BaseNecromancyCreation> getHealableEntities() {
        return level().getEntitiesOfClass(BaseNecromancyCreation.class, getBoundingBox().inflate(5, 2, 5), BaseNecromancyCreation::canBeHeal);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.NECRO_GOLEM_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.NECRO_GOLEM_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.NECRO_GOLEM_HURT.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSoundEvents.NECRO_GOLEM_STEP.get();
    }
}
