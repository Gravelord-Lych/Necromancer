package lych.necromancer.entity.monster;

import lych.necromancer.network.syncher.ModEntityDataSerializers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public abstract non-sealed class NecromancyGenerated extends BaseNecromancyCreation {
    public static final float DEFAULT_R = 0.75F;
    public static final float DEFAULT_G = 0.2F;
    public static final float DEFAULT_B = 1;
    public static final float DEFAULT_A = 1;
    protected static final double DEFAULT_MAX_HEALTH = 20;
    protected static final double DEFAULT_MOVEMENT_SPEED = 0.25;
    protected static final double DEFAULT_ATTACK_DAMAGE = 8;
    protected static final double DEFAULT_FOLLOW_RANGE = 16;
    protected static final double DEFAULT_KNOCKBACK_RESISTANCE = 0;
    protected static final double SMALL_MAX_HEALTH = 20;
    protected static final double SMALL_ATTACK_DAMAGE = 6;
    protected static final double LARGE_MAX_HEALTH = 50;
    protected static final double LARGE_ATTACK_DAMAGE = 12;
    protected static final double LARGE_KNOCKBACK_RESISTANCE = 0.5;
    protected static final float DEFAULT_STANDING_EYE_HEIGHT = 1.74F;
    protected static final float BABY_STANDING_EYE_HEIGHT = 0.93F;
    protected static final int DEFAULT_XP = 5;
    protected static final double ETHEREALITY_AVOID_DAMAGE_PROBABILITY = 0.3;
    protected static final double INSTABILITY_EXPLODE_PROBABILITY_HURT = 0.075;
    protected static final double INSTABILITY_EXPLODE_PROBABILITY_DEATH = 0.3;
    protected static final int REGENERATE_HEALTH_INTERVAL = 20;
    protected static final int REGENERATE_HEALTH_INTERVAL_IN_COMBAT = 60;
    protected static final float REGENERATE_HEALTH_AMOUNT = 1;
    protected static final float REGENERATE_HEALTH_AMOUNT_LARGE = 1.5F;
    protected static final int DEFAULT_EXPLOSION_RADIUS = 3;
    private static final SizeAndPropertyGroupData EMPTY = new SizeAndPropertyGroupData(Size.MEDIUM, null);
    private static final Map<Difficulty, Double> HAS_PROPERTY_PROBABILITY_MAP =
            Map.of(Difficulty.PEACEFUL, 0.0, Difficulty.EASY, 0.0, Difficulty.NORMAL, 0.1, Difficulty.HARD, 0.5);

    private static final EntityDataAccessor<Optional<Property>> DATA_PROPERTY =
            SynchedEntityData.defineId(NecromancyGenerated.class, ModEntityDataSerializers.NECROMANCY_GENERATED_MOB_PROPERTY.get());
    private static final EntityDataAccessor<Size> DATA_SIZE =
            SynchedEntityData.defineId(NecromancyGenerated.class, ModEntityDataSerializers.NECROMANCY_GENERATED_MOB_SIZE.get());
    private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("25D28592-C984-CE45-8CD7-54C6A25989D1");
    private static final AttributeModifier SPEED_MODIFIER_BABY =
            new AttributeModifier(SPEED_MODIFIER_BABY_UUID, "Necromancy-Generated Mob Baby speed boost", 0.5, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final UUID SPEED_PENALTY_LARGE_UUID =
            UUID.fromString("AFE8278A-683D-65F1-FDAA-856BFBF4C8A6");
    private static final AttributeModifier SPEED_PENALTY_LARGE =
            new AttributeModifier(SPEED_PENALTY_LARGE_UUID, "Necromancy-Generated Mob Large speed penalty", -0.2, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final UUID PROPERTY_CAUSED_HEALTH_MODIFIER_UUID = UUID.fromString("C18F43F8-845B-32E5-DEDE-8154D0506B12");
    private static final UUID SPEED_MODIFIER_ETHEREALITY_UUID = UUID.fromString("78B84651-2A7E-6865-2D7C-AE9060617237");
    private static final AttributeModifier SPEED_MODIFIER_ETHEREALITY =
            new AttributeModifier(SPEED_MODIFIER_ETHEREALITY_UUID, "Necromancy-Generated Mob Ethereality speed boost", 0.35, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final String PROPERTY_TAG = "Property";
    private static final String SIZE_TAG = "Size";
    private static final String EXPLOSION_RADIUS_TAG = "ExplosionRadius";
    private int explosionRadius = DEFAULT_EXPLOSION_RADIUS;

    protected NecromancyGenerated(EntityType<? extends NecromancyGenerated> type, Level level) {
        super(type, level);
        xpReward = DEFAULT_XP;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_SIZE, Size.MEDIUM);
        entityData.define(DATA_PROPERTY, Optional.empty());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (DATA_SIZE.equals(accessor)) {
            refreshDimensions();
        }
        super.onSyncedDataUpdated(accessor);
    }

    public static AttributeSupplier.Builder createNecromancyGeneratedMobAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, DEFAULT_MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, DEFAULT_MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, DEFAULT_ATTACK_DAMAGE)
                .add(Attributes.FOLLOW_RANGE, DEFAULT_FOLLOW_RANGE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        getProperty().ifPresent(property -> tag.putString(PROPERTY_TAG, property.name()));
        tag.putString(SIZE_TAG, getSize().name());
        tag.putInt(EXPLOSION_RADIUS_TAG, explosionRadius);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(PROPERTY_TAG, CompoundTag.TAG_STRING)) {
            setProperty(Property.valueOf(tag.getString(PROPERTY_TAG)));
        }
        if (tag.contains(SIZE_TAG, CompoundTag.TAG_STRING)) {
            setSize(Size.valueOf(tag.getString(SIZE_TAG)));
        }
        explosionRadius = tag.getInt(EXPLOSION_RADIUS_TAG);
    }

    @Override
    protected void combatTick() {
        super.combatTick();
        maybeHealRegularly(REGENERATE_HEALTH_INTERVAL_IN_COMBAT);
    }

    @Override
    protected void combatCooldownTick() {
        super.combatCooldownTick();
        maybeHealRegularly(REGENERATE_HEALTH_INTERVAL_IN_COMBAT);
    }

    @Override
    protected void noCombatTick() {
        super.noCombatTick();
        maybeHealRegularly(REGENERATE_HEALTH_INTERVAL);
    }

    protected void maybeHealRegularly(float interval) {
        if (!level().isClientSide() && canBeHeal() && hasProperty(Property.REGENERATION) && tickCount % interval == 0) {
            heal(getSize() == Size.LARGE ? REGENERATE_HEALTH_AMOUNT_LARGE : REGENERATE_HEALTH_AMOUNT);
            addHeartParticles(random.nextIntBetweenInclusive(2, 3));
        }
    }

    protected void addHeartParticles(int count) {
        addHeartParticles(this, count);
    }

    protected void addHeartParticles(LivingEntity entity, int count) {
        if (level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.HEART,
                    entity.getX() + (entity.getRandom().nextDouble() - 0.5) * getScale(),
                    entity.getY() + entity.getRandom().nextDouble() * getBbHeight(),
                    entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * getScale(),
                    count,
                    0,
                    0,
                    0,
                    0);
        } else {
            for (int i = 0; i < count; i++) {
                level().addParticle(ParticleTypes.HEART,
                        entity.getX() + (entity.getRandom().nextDouble() - 0.5) * getScale(),
                        entity.getY() + entity.getRandom().nextDouble() * getBbHeight(),
                        entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * getScale(),
                        0,
                        0,
                        0);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Property property = getProperty().orElse(null);
        if (property != null) {
            switch (property) {
                case ETHEREALITY -> {
                    if (random.nextDouble() < ETHEREALITY_AVOID_DAMAGE_PROBABILITY) {
                        spawnAnim();
                        return false;
                    }
                }
                case INSTABILITY -> {
                    if (random.nextDouble() < INSTABILITY_EXPLODE_PROBABILITY_HURT) {
                        explode();
                        return false;
                    }
                }
            }
        }
        if (source.getEntity() instanceof NecromancyGenerated) {
            amount *= 0.2F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (hasProperty(Property.INSTABILITY) && random.nextDouble() < INSTABILITY_EXPLODE_PROBABILITY_DEATH) {
            explode();
            return;
        }
        super.die(source);
    }

    protected void explode() {
        if (!level().isClientSide()) {
           dead = true;
           level().explode(this, getX(), getY(), getZ(), explosionRadius, Level.ExplosionInteraction.NONE);
           discard();
           spawnLingeringCloud();
        }
    }

    protected void spawnLingeringCloud() {
        Collection<MobEffectInstance> effects = getActiveEffects();
        if (!effects.isEmpty()) {
            AreaEffectCloud cloud = new AreaEffectCloud(level(), getX(), getY(), getZ());
            cloud.setRadius(2.5F);
            cloud.setRadiusOnUse(-0.5F);
            cloud.setWaitTime(10);
            cloud.setDuration(cloud.getDuration() / 2);
            cloud.setRadiusPerTick(-cloud.getRadius() / (float)cloud.getDuration());
            effects.stream().map(MobEffectInstance::new).forEach(cloud::addEffect);
            level().addFreshEntity(cloud);
        }
    }

    public Size getSize() {
        return entityData.get(DATA_SIZE);
    }

    public void setSize(Size size) {
        entityData.set(DATA_SIZE, Objects.requireNonNull(size));
        if (!level().isClientSide()) {
            size.adjustAttributes(this);
        }
    }

    public Optional<Property> getProperty() {
        return entityData.get(DATA_PROPERTY);
    }

    public boolean hasProperty() {
        return getProperty().isPresent();
    }

    public boolean hasProperty(Property property) {
        return getProperty().orElse(null) == property;
    }

    public void setProperty(@Nullable Property property) {
        entityData.set(DATA_PROPERTY, Optional.ofNullable(property));
        if (!level().isClientSide() && property != null) {
            property.modify(this);
        }
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        if (data == null) {
            Property property = null;
            if (random.nextDouble() < HAS_PROPERTY_PROBABILITY_MAP.get(difficulty.getDifficulty())) {
                property = Property.random(random);
            }
            data = new SizeAndPropertyGroupData(Size.random(random), property);
        }
        if (data instanceof SizeAndPropertyGroupData spData) {
            setSize(spData.size());
            setProperty(spData.property());
        }
        return data;
    }

    @Override
    public boolean isBaby() {
        return getSize() == Size.SMALL;
    }

    @Override
    public void setBaby(boolean baby) {
        setSize(baby ? Size.SMALL : Size.MEDIUM);
    }

    @Override
    public float getScale() {
        return getSize().getScale();
    }

    @Override
    public float getVoicePitch() {
        float rand = (random.nextFloat() - random.nextFloat()) * 0.2F;
        return switch (getSize()) {
            case SMALL -> rand + 1.1F;
            case MEDIUM -> rand + 1;
            case LARGE -> rand + 0.85F;
        };
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return switch (getSize()) {
            case SMALL -> BABY_STANDING_EYE_HEIGHT;
            case MEDIUM -> DEFAULT_STANDING_EYE_HEIGHT;
            case LARGE -> DEFAULT_STANDING_EYE_HEIGHT * Size.LARGE.getScale();
        };
    }

    @Override
    public int getExperienceReward() {
        double mul = 0;
        if (getSize() != Size.MEDIUM) {
            mul += 1.5;
        }
        if (hasProperty()) {
            mul += 2;
        }
        xpReward *= (1 + mul);
        return super.getExperienceReward();
    }

    protected void clearSpeedModifiers() {
        getNonnullAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_BABY_UUID);
        getNonnullAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_PENALTY_LARGE_UUID);
    }

    protected void setMaxHealth() {
        setHealth(getMaxHealth());
    }

    protected void adjustAttributesForSizeSmall() {
        getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(SMALL_MAX_HEALTH);
        getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(SMALL_ATTACK_DAMAGE);
        getNonnullAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(DEFAULT_KNOCKBACK_RESISTANCE);
        getNonnullAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(SPEED_MODIFIER_BABY);
    }

    protected void adjustAttributesForSizeMedium() {
        getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(DEFAULT_MAX_HEALTH);
        getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(DEFAULT_ATTACK_DAMAGE);
        getNonnullAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(DEFAULT_KNOCKBACK_RESISTANCE);
    }

    protected void adjustAttributesForSizeLarge() {
        getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(LARGE_MAX_HEALTH);
        getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(LARGE_ATTACK_DAMAGE);
        getNonnullAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(LARGE_KNOCKBACK_RESISTANCE);
        getNonnullAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(SPEED_PENALTY_LARGE);
    }

    protected void applyEtherealityProperty() {
        getNonnullAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(SPEED_MODIFIER_ETHEREALITY);
    }

    protected void applyInstabilityProperty() {}

    protected void applyRegenerationProperty() {}

    protected AttributeInstance getNonnullAttribute(Attribute attribute) {
        return Objects.requireNonNull(getAttribute(attribute), "Requested attribute " + attribute + " is null!");
    }
    
    public static SizeAndPropertyGroupData empty() {
        return EMPTY;
    }

    public static SizeAndPropertyGroupData specifySize(Size size) {
        if (size == Size.MEDIUM) {
            return empty();
        }
        return new SizeAndPropertyGroupData(size, EMPTY.property());
    }
    
    public static SizeAndPropertyGroupData specifyProperty(@Nullable Property property) {
        if (property == null) {
            return empty();
        }
        return new SizeAndPropertyGroupData(EMPTY.size(), property);
    }

    public record SizeAndPropertyGroupData(Size size, @Nullable Property property) implements SpawnGroupData {}

    public enum Size implements WeightedEntry {
        SMALL(NecromancyGenerated::adjustAttributesForSizeSmall, 0.5F, Weight.of(1)),
        MEDIUM(NecromancyGenerated::adjustAttributesForSizeMedium, 1, Weight.of(10)),
        LARGE(NecromancyGenerated::adjustAttributesForSizeLarge, 1.6F, Weight.of(1));

        private final Consumer<? super NecromancyGenerated> attributeAdjuster;
        private final float scale;
        private final Weight weight;

        Size(Consumer<? super NecromancyGenerated> attributeAdjuster, float scale, Weight weight) {
            this.attributeAdjuster = attributeAdjuster;
            this.scale = scale;
            this.weight = weight;
        }

        public float getScale() {
            return scale;
        }

        public void adjustAttributes(NecromancyGenerated necromancyGenerated) {
            necromancyGenerated.clearSpeedModifiers();
            attributeAdjuster.accept(necromancyGenerated);
            necromancyGenerated.setMaxHealth();
        }

        @Override
        public Weight getWeight() {
            return weight;
        }

        public static Size random(RandomSource random) {
            return WeightedRandom.getRandomItem(random, List.of(values())).orElseThrow();
        }
    }

    public enum Property implements WeightedEntry {
        ETHEREALITY(-0.25, 0.1F, 0.6F, 0.8F, 0.75F, Weight.of(1), NecromancyGenerated::applyEtherealityProperty),
        INSTABILITY(-0.25, 0.3F, 1, 0.5F, 1, Weight.of(1), NecromancyGenerated::applyInstabilityProperty),
        REGENERATION(0, 0.9F, 0.3F, 0.2F, 1, Weight.of(1), NecromancyGenerated::applyRegenerationProperty);

        private final double healthModifier;
        private final float r;
        private final float g;
        private final float b;
        private final float a;
        private final Weight weight;
        private final Consumer<? super NecromancyGenerated> additionalModifier;

        Property(double healthModifier, float r, float g, float b, float a, Weight weight, Consumer<? super NecromancyGenerated> additionalModifier) {
            this.healthModifier = healthModifier;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.weight = weight;
            this.additionalModifier = additionalModifier;
        }

        public double getHealthModifier() {
            return healthModifier;
        }

        public float getR() {
            return r;
        }

        public float getG() {
            return g;
        }

        public float getB() {
            return b;
        }

        public float getA() {
            return a;
        }

        public void modify(NecromancyGenerated mob) {
            if (getHealthModifier() != 0) {
                mob.getNonnullAttribute(Attributes.MAX_HEALTH)
                        .addTransientModifier(new AttributeModifier(PROPERTY_CAUSED_HEALTH_MODIFIER_UUID,
                                "Necromancy-Generated Mob health modifier",
                                getHealthModifier(),
                                AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
            additionalModifier.accept(mob);
            mob.setMaxHealth();
        }

        @Override
        public Weight getWeight() {
            return weight;
        }

        public static Property random(RandomSource random) {
            return WeightedRandom.getRandomItem(random, List.of(values())).orElseThrow();
        }
    }
}
