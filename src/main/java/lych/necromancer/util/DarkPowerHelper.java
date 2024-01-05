package lych.necromancer.util;

import lych.necromancer.Necromancer;
import lych.necromancer.capability.IDarkPowerStorage;
import lych.necromancer.capability.ModCapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityProvider;

import java.util.List;
import java.util.NoSuchElementException;

public final class DarkPowerHelper {
    public static final String DP = Necromancer.prefixMsg("item", "dp");

    private DarkPowerHelper() {}

    public static IDarkPowerStorage of(@SuppressWarnings("UnstableApiUsage") CapabilityProvider<?> provider) {
        return provider.getCapability(ModCapabilities.DARK_POWER_STORAGE).orElseThrow(NoSuchElementException::new);
    }

    public static int calculateBarWidth(ItemStack stack) {
        IDarkPowerStorage dps = of(stack);
        float dp = (float) dps.getDarkPower() / dps.getMaxStorage();
        return Math.round(dp * Item.MAX_BAR_WIDTH);
    }

    public static int calculateBarColor(ItemStack stack) {
        IDarkPowerStorage dps = of(stack);
        float dp = (float) dps.getDarkPower() / dps.getMaxStorage();
        return Mth.hsvToRgb(Mth.lerp(dp, 270, 300) / 360, Mth.lerp(dp, 0.7f, 0.8f), Mth.lerp(dp, 0.85f, 1));
    }

    public static void appendDPMessage(ItemStack stack, List<Component> text) {
        IDarkPowerStorage dps = of(stack);
        text.add(Component.translatable(DP, dps.getDarkPower(), dps.getMaxStorage()).withStyle(ChatFormatting.DARK_PURPLE));
    }
}
