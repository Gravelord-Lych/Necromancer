package lych.necromancer.capability;

import lych.necromancer.Necromancer;
import lych.necromancer.capability.player.INecromancerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class ModCapabilities {
    public static final ResourceLocation NECROMANCER_DATA_ID = Necromancer.prefix("necromancer_data");
    public static final Capability<INecromancerData> NECROMANCER_DATA = CapabilityManager.get(new CapabilityToken<>(){});
    public static final ResourceLocation DARK_POWER_STORAGE_ID = Necromancer.prefix("dark_power_storage");
    public static final Capability<IDarkPowerStorage> DARK_POWER_STORAGE = CapabilityManager.get(new CapabilityToken<>(){});

    private ModCapabilities() {}
}
