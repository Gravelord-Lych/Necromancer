package lych.necromancer.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemDarkPowerStorage implements IDarkPowerStorage {
    private static final String DP = "DarkPower";
    private final ItemStack stack;
    private final int maxStorage;

    public ItemDarkPowerStorage(ItemStack stack, int maxStorage) {
        this.stack = stack;
        this.maxStorage = maxStorage;
    }

    @Override
    public int getDarkPower() {
        return stack.hasTag() ? stack.getTag().getInt(DP) : 0;
    }

    @Override
    public void setDarkPower(int amount) {
        stack.getOrCreateTag().putInt(DP, Mth.clamp(amount, 0, getMaxStorage()));
    }

    @Override
    public int getMaxStorage() {
        return maxStorage;
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        @Nullable
        private ItemDarkPowerStorage storage;
        private final ItemStack stack;
        private final int maxStorage;

        public Provider(ItemStack stack, int maxStorage) {
            this.stack = stack;
            this.maxStorage = maxStorage;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == ModCapabilities.DARK_POWER_STORAGE ? LazyOptional.of(() -> get(stack, maxStorage)).cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return new CompoundTag();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {}

        private ItemDarkPowerStorage get(ItemStack stack, int maxStorage) {
            if (storage == null) {
                storage = new ItemDarkPowerStorage(stack, maxStorage);
            }
            return storage;
        }
    }
}
