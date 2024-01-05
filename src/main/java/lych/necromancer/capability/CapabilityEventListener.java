package lych.necromancer.capability;

import lych.necromancer.Necromancer;
import lych.necromancer.capability.player.NecromancerData;
import lych.necromancer.capability.world.FirstStrikeDataStorage;
import lych.necromancer.capability.world.event.BaseWorldEventManager;
import lych.necromancer.capability.world.event.FirstStrikeManager;
import lych.necromancer.capability.world.event.OnslaughtManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Necromancer.MODID)
public final class CapabilityEventListener {
    private CapabilityEventListener() {}

    @SubscribeEvent
    public static void onEntityAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ModCapabilityIDs.NECROMANCER_DATA_ID, new NecromancerData.Provider());
        }
    }

    @SubscribeEvent
    public static void onLevelAttachCapabilities(AttachCapabilitiesEvent<Level> event) {
        if (event.getObject() instanceof ServerLevel level) {
            event.addCapability(ModCapabilityIDs.FIRST_STRIKE_MANAGER_ID, new BaseWorldEventManager.Provider<>(ModCapabilities.FIRST_STRIKE_MANAGER, () -> new FirstStrikeManager(level)));
            event.addCapability(ModCapabilityIDs.FIRST_STRIKE_DATA_STORAGE_ID, new FirstStrikeDataStorage.Provider(level));
            event.addCapability(ModCapabilityIDs.ONSLAUGHT_MANAGER_ID, new BaseWorldEventManager.Provider<>(ModCapabilities.ONSLAUGHT_MANAGER, () -> new OnslaughtManager(level)));
        }
    }

    @SubscribeEvent
    public static void retrieveData(PlayerEvent.Clone event) {
        NecromancerData.transfer(event.getOriginal(), event.getEntity());
    }
}
