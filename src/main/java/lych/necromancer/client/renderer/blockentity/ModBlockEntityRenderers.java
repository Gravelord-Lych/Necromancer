package lych.necromancer.client.renderer.blockentity;

import lych.necromancer.Necromancer;
import lych.necromancer.block.entity.ModBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register;

@Mod.EventBusSubscriber(modid = Necromancer.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModBlockEntityRenderers {
    private ModBlockEntityRenderers() {}

    @SubscribeEvent
    public static void registerBlockEntityRenderers(FMLClientSetupEvent event) {
        register(ModBlockEntities.NECROCK_ITEM_BASE.get(), NecrockItemBaseRenderer::new);
        register(ModBlockEntities.NECROCK_ITEM_CARRIER.get(), NecrockItemCarrierRenderer::new);
    }
}
