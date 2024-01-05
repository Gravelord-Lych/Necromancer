package lych.necromancer.entity.monster;

import lych.necromancer.Necromancer;
import lych.necromancer.world.event.AbstractOnslaught;
import lych.necromancer.world.event.FirstStrike;
import lych.necromancer.world.event.Onslaught;
import lych.necromancer.world.event.Onslaughts;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public abstract sealed class BaseNecromancyCreation extends Monster permits NecromancyGenerated, NecromancyRevived {
    protected static final Marker NECROMANCY_CREATION = MarkerFactory.getMarker("Necro-Mobs");
    protected static final int DEFAULT_MAX_NO_COMBAT_TIME = 120;
    private static final EntityDataAccessor<Integer> DATA_SPAWN_INVUL_TICKS = SynchedEntityData.defineId(BaseNecromancyCreation.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MAX_SPAWN_INVUL_TICKS = SynchedEntityData.defineId(BaseNecromancyCreation.class, EntityDataSerializers.INT);
    private static final String ONSLAUGHT_TAG = "OnslaughtId";
    private static final String NO_COMBAT_TIME_TAG = "NoCombatTime";
    private static final String SPAWN_INVUL_TICKS_TAG = "SpawnInvulTicks";
    private static final String MAX_SPAWN_INVUL_TICKS_TAG = "MaxSpawnInvulTicks";
    protected final OnslaughtHelper helper = new OnslaughtHelper();
    private int noCombatTime;
    private boolean inCombat;

    protected BaseNecromancyCreation(EntityType<? extends BaseNecromancyCreation> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_SPAWN_INVUL_TICKS, 0);
        entityData.define(DATA_MAX_SPAWN_INVUL_TICKS, 0);
    }

    public int getSpawnInvulTicks() {
        return entityData.get(DATA_SPAWN_INVUL_TICKS);
    }

    public int getMaxSpawnInvulTicks() {
        return entityData.get(DATA_MAX_SPAWN_INVUL_TICKS);
    }

    public void setSpawnInvulTicks(int ticks) {
        setSpawnInvulTicks(ticks, true);
    }

    protected void setSpawnInvulTicks(int ticks, boolean updateMax) {
        entityData.set(DATA_SPAWN_INVUL_TICKS, ticks);
        if (updateMax) {
            entityData.set(DATA_MAX_SPAWN_INVUL_TICKS, ticks);
        }
    }

    public float getSpawnPercent() {
        if (getMaxSpawnInvulTicks() == 0) {
            return 1;
        }
        return 1 - getSpawnInvulTicks() / (float) getMaxSpawnInvulTicks();
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(7, new RandomStrollGoal(this, 1));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        addBehaviorGoals();
    }

    protected void addBehaviorGoals() {}

    @Nullable
    public Onslaught getOnslaught() {
        return helper.getOnslaught();
    }

    @Nullable
    public FirstStrike getFirstStrike() {
        return helper.getFirstStrike();
    }

    @Nullable
    public AbstractOnslaught<?, ?> getJoinedEvent() {
        return helper.findJoined();
    }

    public void join(@Nullable AbstractOnslaught<?, ?> evt) {
        helper.join(evt);
    }

    public boolean hasActiveOnslaught() {
        return getJoinedEvent() != null && !getJoinedEvent().isFinished();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(ONSLAUGHT_TAG, getJoinedEvent() == null ? -1 : getJoinedEvent().getId());
        tag.putInt(NO_COMBAT_TIME_TAG, noCombatTime);
        tag.putInt(SPAWN_INVUL_TICKS_TAG, getSpawnInvulTicks());
        tag.putInt(MAX_SPAWN_INVUL_TICKS_TAG, getMaxSpawnInvulTicks());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        int id = tag.getInt(ONSLAUGHT_TAG);
        if (level() instanceof ServerLevel sl && id != -1) {
            join(Onslaughts.getOnslaught(sl, id));
            Necromancer.LOGGER.debug(NECROMANCY_CREATION, "{} joined onslaught {}", this, getJoinedEvent());
        }
        noCombatTime = tag.getInt(NO_COMBAT_TIME_TAG);
        setSpawnInvulTicks(tag.getInt(SPAWN_INVUL_TICKS_TAG), false);
        entityData.set(DATA_MAX_SPAWN_INVUL_TICKS, tag.getInt(MAX_SPAWN_INVUL_TICKS_TAG));
    }

    @Override
    public void tick() {
        super.tick();
        tickCombatStatus();
        if (!level().isClientSide()) {
            if (getSpawnInvulTicks() > 0) {
                setSpawnInvulTicks(getSpawnInvulTicks() - 1, false);
            }
        }
    }

    private void tickCombatStatus() {
        int maxNoCombatTime = getMaxNoCombatTime();

        if (isInCombat()) {
            noCombatTime = 0;
        } else if (noCombatTime < maxNoCombatTime) {
            noCombatTime++;
        }

        if (noCombatTime >= maxNoCombatTime) {
            noCombatTick();
        } else if (noCombatTime > 0) {
            combatCooldownTick();
        } else {
            combatTick();
        }
    }

    protected int getMaxNoCombatTime() {
        return DEFAULT_MAX_NO_COMBAT_TIME;
    }

    protected void combatTick() {}

    protected void combatCooldownTick() {}

    protected void noCombatTick() {}

    @Override
    public void onEnterCombat() {
        super.onEnterCombat();
        inCombat = true;
    }

    @Override
    public void onLeaveCombat() {
        super.onLeaveCombat();
        inCombat = false;
    }

    protected boolean isInCombat() {
        return inCombat;
    }

    protected int getNoCombatTime() {
        return noCombatTime;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (getSpawnInvulTicks() > 0) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected abstract SoundEvent getAmbientSound();

    @Override
    protected abstract SoundEvent getDeathSound();

    @Override
    protected abstract SoundEvent getHurtSound(DamageSource source);

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        super.playSound(getStepSound(), 0.15F, 1);
    }

    protected abstract SoundEvent getStepSound();

    public boolean canBeHeal() {
        return getHealth() < getMaxHealth();
    }

    public final class OnslaughtHelper {
        @Nullable
        private Onslaught onslaught;
        @Nullable
        private FirstStrike firstStrike;

        OnslaughtHelper() {}

        @Nullable
        public Onslaught getOnslaught() {
            return onslaught;
        }

        @Nullable
        public FirstStrike getFirstStrike() {
            return firstStrike;
        }

        @Nullable
        public AbstractOnslaught<?, ?> findJoined() {
            return onslaught == null ? firstStrike : onslaught;
        }

        public void join(@Nullable AbstractOnslaught<?, ?> evt) {
            if (evt instanceof FirstStrike) {
                firstStrike = (FirstStrike) evt;
                onslaught = null;
            } else {
                firstStrike = null;
                onslaught = (Onslaught) evt;
            }
            if (evt != null) {
                evt.onMobAdded(BaseNecromancyCreation.this, false);
            }
        }
    }
}
