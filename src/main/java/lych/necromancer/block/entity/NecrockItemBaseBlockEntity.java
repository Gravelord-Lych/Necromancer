package lych.necromancer.block.entity;

import lych.necromancer.sound.ModSoundEvents;
import lych.necromancer.world.crafting.AbstractNecrocraftingRecipe;
import lych.necromancer.world.crafting.Altar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class NecrockItemBaseBlockEntity extends NecrockItemCarrierBlockEntity {
    private static final List<Vec3i> DELTAS_8 = List.of(new Vec3i(2, 0, -2),
            new Vec3i(2, 0, 0),
            new Vec3i(2, 0, 2),
            new Vec3i(0, 0, 2),
            new Vec3i(-2, 0, 2),
            new Vec3i(-2, 0, 0),
            new Vec3i(-2, 0, -2),
            new Vec3i(0, 0, -2));
    private static final float DELTA_SPIN = 3;
    @Nullable
    private Altar altar;
    private float oSpin;
    private float spin;

    public NecrockItemBaseBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NECROCK_ITEM_BASE.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, NecrockItemBaseBlockEntity base) {
        base.syncSpin();
        base.rotate();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, NecrockItemBaseBlockEntity base) {
        base.checkAltar(level, pos, DELTAS_8);
    }

    private void checkAltar(Level level, BlockPos pos, List<Vec3i> deltas) {
        if (altar == null) {
            List<BlockPos> carrierPositions = new ArrayList<>();
            for (Vec3i delta : deltas) {
                BlockPos carrierPos = pos.offset(delta);
                if (level.getBlockEntity(carrierPos, ModBlockEntities.NECROCK_ITEM_CARRIER.get()).isPresent()) {
                    carrierPositions.add(carrierPos);
                } else {
                    return;
                }
            }
            altar = new Altar(level, pos, carrierPositions);
        } else if (!altar.validate()) {
            altar = null;
        }
    }

    @Nullable
    public Altar getAltar() {
        return altar;
    }

    @Override
    public SoundEvent getAddItemSound() {
        return ModSoundEvents.NECROCK_ITEM_BASE_PLACE.get();
    }

    @Override
    public SoundEvent getRemoveItemSound() {
        return ModSoundEvents.NECROCK_ITEM_BASE_REMOVE.get();
    }

    public Optional<AbstractNecrocraftingRecipe> getCraftableRecipe() {
        if (altar == null) {
            return Optional.empty();
        }
        return altar.getCraftableRecipe(level == null ? null : level.getServer());
    }

    @Override
    public BlockPos getSpawnItemPos(BlockPos myPos) {
        return myPos.above();
    }

    public float getSpin(float partialTicks) {
        if (oSpin > spin) { // Prevent "flashes" when “spin %= 360“
            return Mth.lerp(partialTicks, oSpin, spin + 360);
        }
        return Mth.lerp(partialTicks, oSpin, spin);
    }

    private void rotate() {
        spin += NecrockItemBaseBlockEntity.DELTA_SPIN;
        spin %= 360;
    }

    private void syncSpin() {
        oSpin = spin;
    }
}
