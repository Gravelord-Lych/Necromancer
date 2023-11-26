package lych.necromancer.client.item;

import lych.necromancer.Necromancer;
import lych.necromancer.item.ModSpecialItems;
import lych.necromancer.item.NecrowandItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Necromancer.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public final class ModItemProperties {
    private ModItemProperties() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ItemProperties.register(ModSpecialItems.NECROWAND.get(), NecrowandItem.LEVEL_KEY, (stack, level, entity, seed) -> NecrowandItem.getLevel(stack));
    }
}
