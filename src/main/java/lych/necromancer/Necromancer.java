package lych.necromancer;

import com.mojang.logging.LogUtils;
import lych.necromancer.block.ModBlockGroups;
import lych.necromancer.block.ModBlocks;
import lych.necromancer.block.entity.ModBlockEntities;
import lych.necromancer.entity.ModEntities;
import lych.necromancer.item.ModCommonItems;
import lych.necromancer.network.syncher.ModEntityDataSerializers;
import lych.necromancer.sound.ModSoundEvents;
import lych.necromancer.util.tab.ModCreativeModeTabs;
import lych.necromancer.world.crafting.ModRecipeSerializers;
import lych.necromancer.world.crafting.ModRecipeTypes;
import lych.necromancer.world.level.ModGameRules;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(Necromancer.MODID)
public class Necromancer {
    public static final String MODID = "necromancer";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final String MESSAGE_PREFIX = "message";

    public Necromancer() {
        SharedConstants.IS_RUNNING_IN_IDE = true;
        SharedConstants.CHECK_DATA_FIXER_SCHEMA = false;

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        bus.addListener(this::commonSetup);

        ModBlocks.BLOCKS.register(bus);
        ModBlockGroups.init();
        ModCommonItems.ITEMS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
        ModRecipeTypes.RECIPE_TYPES.register(bus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        ModSoundEvents.SOUND_EVENTS.register(bus);
        ModEntityDataSerializers.DATA_SERIALIZERS.register(bus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
        ModGameRules.init();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name);
    }

    public static ResourceLocation prefixTex(String name) {
        return prefix("textures/" + name);
    }

    public static String prefixMsg(String name) {
        return prefixMsg(MESSAGE_PREFIX, name);
    }

    public static String prefixMsg(String type, String name) {
        return type + "." + MODID + "." + name;
    }

    public static String prefixCmd(String type, String name) {
        return prefixMsg("commands", type + "." + name);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
