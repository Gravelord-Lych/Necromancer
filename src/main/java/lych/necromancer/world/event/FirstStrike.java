package lych.necromancer.world.event;

import lych.necromancer.Necromancer;
import lych.necromancer.entity.ModEntities;
import lych.necromancer.entity.monster.BaseNecromancyCreation;
import lych.necromancer.util.AttributeModifierPair;
import lych.necromancer.world.event.context.SingleTargetContext;
import lych.necromancer.world.event.round.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class FirstStrike extends AbstractOnslaught<FirstStrike, SingleTargetContext> {
    public static final String NAME = "first_strike";
    public static final String ID = Necromancer.prefixMsg(EVENT, NAME);
    public static final HintProvider<FirstStrike> HINT_START_1 = makeAmbientHintComponent("start1");
    public static final HintProvider<FirstStrike> HINT_START_2 = makeAmbientHintComponent("start2");
    public static final HintProvider<FirstStrike> HINT_WARNING = makeWarningHintComponent("warning");
    public static final HintProvider<FirstStrike> HINT_R1 = makeWarningHintComponent("r1");
    public static final HintProvider<FirstStrike> HINT_R2 = makeWarningHintComponent("r2");
    public static final HintProvider<FirstStrike> HINT_R3 = makeWarningHintComponent("r3");
    public static final HintProvider<FirstStrike> HINT_R4 = makeWarningHintComponent("r4");
    private static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("B409E314-25AD-9B67-D7F2-E0555987CF53");
    private static final UUID FOLLOW_RANGE_BONUS_UUID = UUID.fromString("779C3799-8237-6D1A-495E-F9C47E254836");
    private static final AttributeModifier FOLLOW_RANGE_BONUS = new AttributeModifier(FOLLOW_RANGE_BONUS_UUID, "First Strike mob follow range bonus", 0.75, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final Rounds<FirstStrike> ROUNDS;

    static {
        RoundMobModifier<BaseNecromancyCreation, FirstStrike> globalModifier = RoundMobModifier.<BaseNecromancyCreation, FirstStrike>builder()
                .modify(fs -> AttributeModifierPair.permanent(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID,
                        "First Strike mob attack damage nerf",
                        getAttackDamageNerf(fs),
                        AttributeModifier.Operation.MULTIPLY_BASE)))
                .modify(AttributeModifierPair.permanent(Attributes.FOLLOW_RANGE, FOLLOW_RANGE_BONUS))
                .also(FirstStrike::resetTarget)
                .also((mob, fs) -> mob.setSpawnInvulTicks(40))
                .build();

        ROUNDS = Rounds
                .<FirstStrike>at(1)
                .modifier(globalModifier)
                .spawns(round()
                        .addHint(1, HINT_START_1)
                        .addHint(100, HINT_START_2)
                        .addHint(DEFAULT_PRE_SPAWN_TICKS - 100, HINT_WARNING)
                        .addHint(DEFAULT_PRE_SPAWN_TICKS, HINT_R1)
                        .addMob(RoundMob.of(ModEntities.NECRO_GOLEM.get(), RoundMob.empty(), 4)))
                .at(2)
                .spawns(round()
                        .addHint(DEFAULT_PRE_SPAWN_TICKS, HINT_R2)
                        .addMob(RoundMob.of(ModEntities.NECRO_GOLEM.get(), RoundMob.empty(), 6)))
                .at(3)
                .spawns(round()
                        .addHint(DEFAULT_PRE_SPAWN_TICKS, HINT_R3)
                        .addMob(RoundMob.of(ModEntities.NECRO_GOLEM.get(), RoundMob.noProperties(), 6)))
                .at(4)
                .spawns(round()
                        .addHint(DEFAULT_PRE_SPAWN_TICKS, HINT_R4)
                        .addMob(RoundMob.of(ModEntities.NECRO_GOLEM.get(), RoundMob.noProperties(), 8)))
                .createRounds();
    }

    private static double getAttackDamageNerf(FirstStrike fs) {
        return switch (fs.level().getDifficulty()) {
            case EASY -> -0.2;
            case NORMAL -> -0.3;
            case HARD -> -0.4;
            case PEACEFUL -> -0.9;
        };
    }

    private static Round.Builder<FirstStrike> round() {
        return Round.round();
    }

    private static HintProvider<FirstStrike> makeAmbientHintComponent(String start1) {
        return makeHintComponent(NAME, start1, style -> style.withColor(ChatFormatting.GRAY).withItalic(true));
    }

    private static HintProvider<FirstStrike> makeWarningHintComponent(String start1) {
        return makeHintComponent(NAME, start1, style -> style.withColor(ChatFormatting.GOLD));
    }

    public FirstStrike(int id, BlockPos center, ServerLevel level, SingleTargetContext context) {
        super(id, center, level, ROUNDS, context);
    }

    public FirstStrike(CompoundTag tag, ServerLevel level) {
        super(tag, ROUNDS, level);
    }

    private static void resetTarget(BaseNecromancyCreation mob, FirstStrike fs) {
        Player target = fs.getSingleTarget();
        if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            mob.setTarget(target);
        }
    }

    @Override
    protected Component getBossEventName() {
        if (isFinished() && !isStopped()) {
            return makeComponent(NAME).copy().append(" - ").append(getStatus().getTranslationKey());
        }
        return makeComponent(NAME);
    }

    @Override
    protected SpawnPositionProvider createPositionProvider() {
        return new SpawnPositionProvider(this, 9, 18);
    }

    @Override
    protected SingleTargetContext loadContext(CompoundTag nbt) {
        return new SingleTargetContext(nbt);
    }
}
