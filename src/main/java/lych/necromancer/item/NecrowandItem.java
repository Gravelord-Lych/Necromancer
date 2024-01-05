package lych.necromancer.item;

import lych.necromancer.Necromancer;
import lych.necromancer.capability.ItemDarkPowerStorage;
import lych.necromancer.capability.ModCapabilities;
import lych.necromancer.util.DarkPowerHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class NecrowandItem extends Item {
    public static final ResourceLocation LEVEL_KEY = Necromancer.prefix("necrowand_level");
    public static final int MAX_LEVEL = 5;
    private static final int BASE_MAX_STORAGE = 1000;
    private static final String LEVEL = "NecrowandLevel";
    private static final String OWNER = "NecrowandOwner";
    private static final String ICON = "IsIcon";

    public NecrowandItem(Properties properties) {
        super(properties);
    }

    public static int getLevel(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(LEVEL) : 1;
    }

    public static void setLevel(ItemStack stack, int level) {
        stack.getOrCreateTag().putInt(LEVEL, Mth.clamp(level, 1, MAX_LEVEL));
    }

    @Nullable
    public static UUID getOwnerUUID(ItemStack stack) {
        return stack.hasTag() && stack.getTag().hasUUID(OWNER) ? stack.getTag().getUUID(OWNER) : null;
    }

    @Nullable
    public static Player getOwner(ItemStack stack, Level level) {
        UUID ownerUUID = getOwnerUUID(stack);
        if (ownerUUID == null) {
            return null;
        }
        return level.getPlayerByUUID(ownerUUID);
    }

    public static boolean hasOwner(ItemStack stack) {
        return getOwnerUUID(stack) != null;
    }

    public static void setOwnerUUID(ItemStack stack, @Nullable UUID uuid) {
        if (uuid == null) {
            stack.getOrCreateTag().remove(OWNER);
        } else {
            stack.getOrCreateTag().putUUID(OWNER, uuid);
        }
    }

    public static void setOwner(ItemStack stack, @Nullable Player player) {
        setOwnerUUID(stack, player == null ? null : player.getUUID());
    }

    public static ItemStack leveledNecrowand(int level) {
        return leveledNecrowand(level, getMaxStorage(level));
    }

    public static ItemStack leveledNecrowand(int level, int dp) {
        ItemStack stack = ModSpecialItems.NECROWAND.get().getDefaultInstance();
        setLevel(stack, level);
        stack.getCapability(ModCapabilities.DARK_POWER_STORAGE).ifPresent(dps -> dps.setDarkPower(dp));
        return stack;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ItemDarkPowerStorage.Provider(stack, getMaxStorage(stack));
    }

    public static int getMaxStorage(ItemStack stack) {
        return getMaxStorage(getLevel(stack));
    }

    public static int getMaxStorage(int level) {
        return BASE_MAX_STORAGE * (1 << (level - 1));
    }

    public static boolean isIcon(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(ICON);
    }

    public static void setIcon(ItemStack stack, boolean icon) {
        stack.getOrCreateTag().putBoolean(ICON, icon);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !isIcon(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return DarkPowerHelper.calculateBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return DarkPowerHelper.calculateBarColor(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasOwner(stack);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        int level = getLevel(stack);
        return switch (level) {
            case 1 -> Rarity.COMMON;
            case 2 -> Rarity.UNCOMMON;
            case 3 -> Rarity.RARE;
            case 4 -> Rarity.EPIC;
            case 5 -> ModRarities.LEGENDARY;
            default -> throw new IllegalStateException("Invalid necrowand level: " + level);
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, level, text, flag);
        DarkPowerHelper.appendDPMessage(stack, text);
    }
}
