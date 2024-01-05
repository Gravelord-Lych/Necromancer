package lych.necromancer.capability.world;

import lych.necromancer.capability.ModCapabilities;
import lych.necromancer.capability.world.event.IFirstStrikeDataStorage;
import lych.necromancer.item.ModCommonItems;
import lych.necromancer.world.event.context.SingleTargetContext;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class FirstStrikeDataStorage implements IFirstStrikeDataStorage {
    private static final String STRUCK_TAG = "Struck";

    private final ServerLevel level;
    private final Set<UUID> struck = new HashSet<>();

    public FirstStrikeDataStorage(ServerLevel level) {
        this.level = level;
    }

    @Override
    public boolean hasStruck(UUID playerUUID) {
        return struck.contains(playerUUID);
    }

    @Override
    public boolean setStruck(UUID playerUUID, boolean struck) {
        return struck ? this.struck.add(playerUUID) : this.struck.remove(playerUUID);
    }

    @Override
    public boolean enableFirstStrikes() {
        return IFirstStrikeDataStorage.enableFirstStrikes(level);
    }

    @Override
    public void tick() {
        for (ServerPlayer player : level.getPlayers(LivingEntity::isAlive)) {
            if (hasStruck(player)) {
                continue;
            }
            if (enableFirstStrikes()) {
                if (!player.gameMode.isSurvival()) {
                    continue;
                }
                tryToStartFirstStrikeFor(player, true);
            } else {
//              Try to give player necrodusts
                tryToStartFirstStrikeFor(player, false);
            }
        }
    }

    @Override
    public boolean tryToStartFirstStrikeFor(ServerPlayer player, boolean considerTime) {
        if (!enableFirstStrikes()) {
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModCommonItems.NECRODUST.get(), 8));
            return setStruck(player.getUUID());
        } else if (!considerTime || level.isNight()) {
            AtomicBoolean result = new AtomicBoolean(true);
            level.getCapability(ModCapabilities.FIRST_STRIKE_MANAGER).resolve()
                    .ifPresentOrElse(manager -> manager.createEvent(player.blockPosition(), new SingleTargetContext(player.getUUID())), () -> result.set(false));
            boolean struck = setStruck(player.getUUID());
            result.set(result.get() && struck);
            return result.get();
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (UUID uuid : struck) {
            listTag.add(NbtUtils.createUUID(uuid));
        }
        tag.put(STRUCK_TAG, listTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains(STRUCK_TAG, CompoundTag.TAG_LIST)) {
            struck.clear();
            ListTag listTag = nbt.getList(STRUCK_TAG, CompoundTag.TAG_INT_ARRAY);
            for (Tag tag : listTag) {
                struck.add(NbtUtils.loadUUID(tag));
            }
        }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        @Nullable
        private FirstStrikeDataStorage data;
        private final ServerLevel level;

        public Provider(ServerLevel level) {
            this.level = level;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == ModCapabilities.FIRST_STRIKE_DATA_STORAGE ? LazyOptional.of(this::get).cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return get().serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            get().deserializeNBT(nbt);
        }

        private FirstStrikeDataStorage get() {
            if (data == null) {
                data = new FirstStrikeDataStorage(level);
            }
            return data;
        }
    }
}
