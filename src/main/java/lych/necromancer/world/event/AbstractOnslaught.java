package lych.necromancer.world.event;

import lych.necromancer.Necromancer;
import lych.necromancer.entity.monster.BaseNecromancyCreation;
import lych.necromancer.world.event.context.TargetContext;
import lych.necromancer.world.event.round.Hint;
import lych.necromancer.world.event.round.HintProvider;
import lych.necromancer.world.event.round.Round;
import lych.necromancer.world.event.round.Rounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;

public abstract sealed class AbstractOnslaught<T extends AbstractOnslaught<T, C>, C extends TargetContext> extends AbstractWorldEvent<C, BaseNecromancyCreation> implements TargetedWorldEvent<C, BaseNecromancyCreation> permits Onslaught, FirstStrike {
    private static final String PRE_SPAWN_TICKS_TAG = "PreSpawnTicks";
    private static final String ROUND_MAX_HEALTH_TAG = "RoundMaxHealth";
    private static final String CURRENT_ROUND_TAG = "CurrentRound";
    private static final String VICTORY_COOLDOWN_TICKS_TAG = "VictoryCooldownTicks";
    protected static final int DEFAULT_PRE_SPAWN_TICKS = 20 * 15;
    protected static final int DEFAULT_POST_EVENT_TICKS = 20 * 2;
    private final Set<BaseNecromancyCreation> members = new HashSet<>();
    private final SpawnPositionProvider provider = createPositionProvider();
    private final Rounds<T> rounds;
    private int currentRound;
    private int preSpawnTicks; // ticks before the round starts
    private int victoryCooldownTicks; // ticks before victory
    private float roundMaxHealth;

    protected AbstractOnslaught(int id, BlockPos center, ServerLevel level, Rounds<T> rounds, C context) {
        super(id, center, level, context);
        this.rounds = rounds;
    }

    protected AbstractOnslaught(CompoundTag tag, Rounds<T> rounds, ServerLevel level) {
        super(tag, level);
        this.rounds = rounds;
    }

    protected static <T extends AbstractWorldEvent<?, ?>> HintProvider<T> makeHintComponent(String name, String text, UnaryOperator<Style> styleOp) {
        return new HintProvider<>(Necromancer.prefixMsg(EVENT, formatHintName(name, text)), (event, player) -> makeHintComponent(name, text, player.getDisplayName()).copy().withStyle(styleOp));
    }

    protected static Component makeHintComponent(String name, String text, Component playerName) {
        return makeComponent(formatHintName(name, text), playerName);
    }

    protected static String formatHintName(String name, String text) {
        return "%s.%s.%s".formatted(name, Hint.NAME, text);
    }

    private static boolean invalid(LivingEntity entity) {
        return entity == null || !entity.isAlive();
    }

    @Override
    protected ServerBossEvent initBossEvent() {
        return new ServerBossEvent(getBossEventName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    }

    protected abstract Component getBossEventName();

    protected abstract SpawnPositionProvider createPositionProvider();

    public SpawnPositionProvider positionProvider() {
        return provider;
    }

    @Override
    protected void eventTick() {
        event.setName(getBossEventName());
        event.setProgress(Mth.clamp(getPercent(), 0, 1));
        event.setVisible(isActive());
        members.removeIf(AbstractOnslaught::invalid);
        if (preSpawnTicks < getDefaultPreSpawnTicks()) {
            preSpawnTicks++;
            checkAndSendHints();
            if (preSpawnTicks == getDefaultPreSpawnTicks()) {
                currentRound++;
                startRound();
            }
            return;
        }
        if (getPercent() == 0) {
            if (rounds.isFinalRound(currentRound)) {
                victoryCooldownTicks++;
                if (victoryCooldownTicks >= getDefaultPostEventTicks()) {
                    setStatus(Status.VICTORY);
                }
            } else {
                endRound();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void checkAndSendHints() {
        Round<T> nextRound = rounds.get(currentRound + 1);
        if (nextRound != null) {
            Hint<T> hint = nextRound.getHintAt(preSpawnTicks);
            if (hint != null) {
                viewTargets().forEach(player -> player.sendSystemMessage(hint.message().getHint((T) this, player)));
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        event.removeAllPlayers();
    }

    @Override
    public void updateTarget(UUID newTarget) {
        if (level().getPlayerByUUID(newTarget) instanceof ServerPlayer player) {
            event.addPlayer(player);
        }
    }

    @SuppressWarnings("unchecked")
    protected void startRound() {
        Round<T> round = getCurrentRound();
        round.spawnAll((T) this);
        roundMaxHealth = getTotalMaxHealth();
    }

    protected void endRound() {
        preSpawnTicks = 0;
    }

    protected float getPercent() {
        if (preSpawnTicks < getDefaultPreSpawnTicks()) {
            return Mth.clamp((float) preSpawnTicks / getDefaultPreSpawnTicks(), 0, 1);
        }
        return getTotalHealth() / roundMaxHealth;
    }

    protected final Round<T> getCurrentRound() {
        Round<T> round = rounds.get(currentRound);
        Objects.requireNonNull(round, "Round %d is null!".formatted(currentRound));
        return round;
    }

    protected float getTotalHealth() {
        float totalHealth = 0;
        for (BaseNecromancyCreation mob : getOnslaughtMembers()) {
            if (Objects.equals(mob.getJoinedEvent(), this)) {
                totalHealth += mob.getHealth();
            }
        }
        return totalHealth;
    }

    protected float getTotalMaxHealth() {
        float totalMaxHealth = 0;
        for (BaseNecromancyCreation mob : getOnslaughtMembers()) {
            if (Objects.equals(mob.getJoinedEvent(), this)) {
                totalMaxHealth += mob.getMaxHealth();
            }
        }
        return totalMaxHealth;
    }

    @NotNull
    private Set<BaseNecromancyCreation> getOnslaughtMembers() {
        return members;
    }

    protected int getDefaultPreSpawnTicks() {
        return DEFAULT_PRE_SPAWN_TICKS;
    }

    protected int getDefaultPostEventTicks() {
        return DEFAULT_POST_EVENT_TICKS;
    }

    @Override
    protected void postEventTick() {

    }

    @Override
    public void onMobAdded(BaseNecromancyCreation mob, boolean updateBossBar) {
        members.add(mob);
        if (updateBossBar) {
            roundMaxHealth = getTotalMaxHealth();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt(PRE_SPAWN_TICKS_TAG, preSpawnTicks);
        nbt.putFloat(ROUND_MAX_HEALTH_TAG, roundMaxHealth);
        nbt.putInt(CURRENT_ROUND_TAG, currentRound);
        nbt.putInt(VICTORY_COOLDOWN_TICKS_TAG, victoryCooldownTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        preSpawnTicks = nbt.getInt(PRE_SPAWN_TICKS_TAG);
        roundMaxHealth = nbt.getFloat(ROUND_MAX_HEALTH_TAG);
        currentRound = nbt.getInt(CURRENT_ROUND_TAG);
        victoryCooldownTicks = nbt.getInt(VICTORY_COOLDOWN_TICKS_TAG);
    }
}
