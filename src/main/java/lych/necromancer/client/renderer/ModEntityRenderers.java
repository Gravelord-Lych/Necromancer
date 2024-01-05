package lych.necromancer.client.renderer;

import lych.necromancer.Necromancer;
import lych.necromancer.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Necromancer.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public final class ModEntityRenderers {
    private ModEntityRenderers() {}

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.NECRO_GOLEM, CommonLayerDefinitionFactory::createBaseHumanoid);
    }

    @SubscribeEvent
    public static void onRegisterEntityRenderers(RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.NECRO_GOLEM.get(), NecroGolemRenderer::new);
    }
}
