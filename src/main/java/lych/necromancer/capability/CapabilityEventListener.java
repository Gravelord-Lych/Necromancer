package lych.necromancer.capability;

import lych.necromancer.Necromancer;
import lych.necromancer.capability.player.NecromancerData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
            event.addCapability(ModCapabilities.NECROMANCER_DATA_ID, new NecromancerData.Provider());
        }
    }

    @SubscribeEvent
    public static void retrieveData(PlayerEvent.Clone event) {
        NecromancerData.transfer(event.getOriginal(), event.getEntity());
    }
}
