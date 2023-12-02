package lych.necromancer.data;

import lych.necromancer.Necromancer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Necromancer.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModDataGens {
    private ModDataGens() {}

    @SubscribeEvent
    public static void registerDataGens(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

        BlockTagDataGen blockTags = new BlockTagDataGen(output, registries, helper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new AdvancementDataGen(output, registries, helper));
        gen.addProvider(event.includeServer(), new ItemTagDataGen(output, registries, blockTags.contentsGetter(), helper));
        gen.addProvider(event.includeServer(), new LootDataGen(output));
        gen.addProvider(event.includeServer(), new RecipeDataGen(output));

        gen.addProvider(event.includeClient(), new BlockStateDataGen(output, helper));
        gen.addProvider(event.includeClient(), new ItemModelDataGen(output, helper));
        gen.addProvider(event.includeClient(), new LangDataGen.EnUs(output));
        gen.addProvider(event.includeClient(), new SoundDataGen(output, helper));
    }
}
