package lych.necromancer.capability.player;

import lych.necromancer.capability.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static lych.necromancer.util.KeepInventoryHelper.*;

public class NecromancerData implements INecromancerData {
    private static final String KEEP_INVENTORY_TIMES_ID = "KeepInventoryTimes";
    private int keepInventoryTimes = DEFAULT_KEEP_INVENTORY_TIMES;

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEEP_INVENTORY_TIMES_ID, keepInventoryTimes);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        keepInventoryTimes = nbt.getInt(KEEP_INVENTORY_TIMES_ID);
    }

    @Override
    public int getKeepInventoryTimes() {
        return keepInventoryTimes;
    }

    @Override
    public void setKeepInventoryTimes(int keepInventoryTimes) {
        if (keepInventoryTimes == INFINITE_KEEP_INVENTORY_TIMES) {
            this.keepInventoryTimes = INFINITE_KEEP_INVENTORY_TIMES;
        } else {
            this.keepInventoryTimes = Mth.clamp(keepInventoryTimes, 0, MAX_KEEP_INVENTORY_TIMES);
        }
    }

    public static void transfer(Player oldPlayer, Player newPlayer) {
        newPlayer.getCapability(ModCapabilities.NECROMANCER_DATA).ifPresent(newCap -> {
            oldPlayer.reviveCaps();
            oldPlayer.getCapability(ModCapabilities.NECROMANCER_DATA).ifPresent(oldCap -> {
                newCap.setKeepInventoryTimes(oldCap.getKeepInventoryTimes());
            });
            oldPlayer.invalidateCaps();
        });
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private NecromancerData data;

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return LazyOptional.of(this::getNecromancerData).cast();
        }

        @Override
        public CompoundTag serializeNBT() {
            return getNecromancerData().serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            getNecromancerData().deserializeNBT(nbt);
        }

        private NecromancerData getNecromancerData() {
            if (data == null) {
                data = new NecromancerData();
            }
            return data;
        }
    }
}
