package lych.necromancer.data;

import lych.necromancer.Necromancer;
import lych.necromancer.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockStateDataGen extends BlockStateProvider {
    public BlockStateDataGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Necromancer.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModBlocks.getAllGroups().forEach(group -> group.generateBlockStates(this));
        simpleBlock(ModBlocks.NECROITE_BLOCK.get());
        getVariantBuilder(ModBlocks.NECROCK_ITEM_CARRIER.get())
                .partialState()
                .addModels(new ConfiguredModel(models()
                        .withExistingParent(ForgeRegistries.BLOCKS.getKey(ModBlocks.NECROCK_ITEM_CARRIER.get()).toString(), Necromancer.prefix("template_item_carrier"))
                        .texture("top", Necromancer.prefix("block/polished_necrock"))));
    }
}
