package lych.necromancer.data;

import lych.necromancer.Necromancer;
import lych.necromancer.block.ModBlockNames;
import lych.necromancer.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Consumer;

public class BlockStateDataGen extends BlockStateProvider {
    private static final String TEMPLATE_ITEM_CARRIER = "template_item_carrier";
    private static final String TEMPLATE_ITEM_BASE = "template_item_base";

    public BlockStateDataGen(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Necromancer.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModBlocks.getAllGroups().forEach(group -> group.generateBlockStates(this));
        simpleBlock(ModBlocks.NECROITE_BLOCK.get());
        templateAltarElement(ModBlocks.NECROCK_ITEM_CARRIER.get(), TEMPLATE_ITEM_CARRIER, ModBlockNames.POLISHED_NECROCK);
        templateAltarElement(ModBlocks.NECROCK_ITEM_BASE.get(), TEMPLATE_ITEM_BASE, ModBlockNames.POLISHED_NECROCK);
    }

    protected void templateAltarElement(Block block, String template, String material) {
        template(block, Necromancer.prefix(template), builder -> builder
                .texture("particle", Necromancer.prefix("block/%s".formatted(material)))
                .texture("top", Necromancer.prefix("block/%s".formatted(material))));
    }

    protected void template(Block block, ResourceLocation template, Consumer<? super BlockModelBuilder> keysAdder) {
        BlockModelBuilder builder = models().withExistingParent(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).toString(), template);
        keysAdder.accept(builder);

        getVariantBuilder(block)
                .partialState()
                .addModels(new ConfiguredModel(builder));
    }
}
