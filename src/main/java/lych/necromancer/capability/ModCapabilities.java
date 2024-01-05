package lych.necromancer.capability;

import lych.necromancer.capability.player.INecromancerData;
import lych.necromancer.capability.world.event.FirstStrikeManager;
import lych.necromancer.capability.world.event.IFirstStrikeDataStorage;
import lych.necromancer.capability.world.event.OnslaughtManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class ModCapabilities {
    public static final Capability<INecromancerData> NECROMANCER_DATA = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IDarkPowerStorage> DARK_POWER_STORAGE = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IFirstStrikeDataStorage> FIRST_STRIKE_DATA_STORAGE = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<FirstStrikeManager> FIRST_STRIKE_MANAGER = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<OnslaughtManager> ONSLAUGHT_MANAGER = CapabilityManager.get(new CapabilityToken<>(){});

    private ModCapabilities() {}
}
