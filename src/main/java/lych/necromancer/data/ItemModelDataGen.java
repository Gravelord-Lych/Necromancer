package lych.necromancer.data;

import lych.necromancer.Necromancer;
import lych.necromancer.block.BlockEntry;
import lych.necromancer.block.BlockGroup;
import lych.necromancer.block.ModBlockGroups;
import lych.necromancer.block.ModBlocks;
import lych.necromancer.item.ModSpawnEggs;
import lych.necromancer.item.NecrowandItem;
import lych.necromancer.util.ReflectionUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

import static lych.necromancer.item.ModCommonItems.*;
import static lych.necromancer.item.ModSpecialItems.NECROWAND;

public class ItemModelDataGen extends ItemModelProvider {
    private static final ModelFile GENERATED = new UncheckedModelFile("item/generated");
    private static final ModelFile HANDHELD = new UncheckedModelFile("item/handheld");
    private static final String LAYER0 = "layer0";
    private static final String LAYER1 = "layer1";

    public ItemModelDataGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Necromancer.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(NECRODUST.get());
        basicItem(NECROITE_INGOT.get());
        basicItem(NECROITE_NUGGET.get());
        getBuilder(NECROWAND.get().toString())
                .parent(HANDHELD)
                .texture(LAYER0, getItemLocation(NECROWAND.get(), "_lv1"))
                .override()
                .predicate(NecrowandItem.LEVEL_KEY, 1).model(new UncheckedModelFile(getItemLocation(NECROWAND.get(), "_lv1"))).end().override()
                .predicate(NecrowandItem.LEVEL_KEY, 2).model(new UncheckedModelFile(getItemLocation(NECROWAND.get(), "_lv2"))).end().override()
                .predicate(NecrowandItem.LEVEL_KEY, 3).model(new UncheckedModelFile(getItemLocation(NECROWAND.get(), "_lv3"))).end().override()
                .predicate(NecrowandItem.LEVEL_KEY, 4).model(new UncheckedModelFile(getItemLocation(NECROWAND.get(), "_lv4"))).end().override()
                .predicate(NecrowandItem.LEVEL_KEY, 5).model(new UncheckedModelFile(getItemLocation(NECROWAND.get(), "_lv5"))).end();
        basicHandheldItem(NECROWAND.get(), "_lv1");
        basicHandheldItem(NECROWAND.get(), "_lv2");
        basicHandheldItem(NECROWAND.get(), "_lv3");
        basicHandheldItem(NECROWAND.get(), "_lv4");
        basicHandheldItem(NECROWAND.get(), "_lv5");

        ReflectionUtils.iterateFields(ModBlockGroups.class, BlockGroup.class, field -> ((BlockGroup) field.get(null)).forAllEntries(this::defaultBlockItem));
        ReflectionUtils.iterateFields(ModBlocks.class, BlockEntry.class, field -> defaultBlockItem((BlockEntry<?, ?>) field.get(null)));
        autoGenerateForSpawnEggs();
    }

    @SuppressWarnings("unchecked")
    private void autoGenerateForSpawnEggs() {
        ReflectionUtils.iterateFields(ModSpawnEggs.class, RegistryObject.class, field -> basicSpawnEggItem(((RegistryObject<? extends Item>) field.get(null)).get()));
    }

    public ItemModelBuilder basicSpawnEggItem(Item item) {
        return getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/template_spawn_egg"));
    }

    private void defaultBlockItem(BlockEntry<?, ?> entry) {
        if (entry.usesDefaultBlockItemModel()) {
            ResourceLocation blockLocation = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(entry.get()));
            getBuilder(entry.item().toString()).parent(new UncheckedModelFile(getPath(entry, blockLocation)));
        }
    }

    private static String getPath(BlockEntry<?, ?> entry, ResourceLocation blockLocation) {
        StringBuilder res = new StringBuilder().append(blockLocation.getNamespace()).append(":block/").append(blockLocation.getPath());
        if (entry.get() instanceof WallBlock || entry.get() instanceof FenceBlock) {
            res.append("_inventory");
        }
        return res.toString();
    }

    private static ResourceLocation getItemLocation(Item item) {
        return getItemLocation(item, "");
    }

    private static ResourceLocation getItemLocation(Item item, String addend) {
        return getItemLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), addend);
    }

    private static ResourceLocation getItemLocation(ResourceLocation location, String addend) {
        return new ResourceLocation(location.getNamespace(), "item/" + location.getPath() + addend);
    }

    public ItemModelBuilder basicHandheldItem(Item item) {
        return basicHandheldItem(item, "");
    }

    public ItemModelBuilder basicHandheldItem(Item item, String addend) {
        return basicHandheldItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), addend);
    }

    public ItemModelBuilder basicHandheldItem(ResourceLocation item, String addend) {
        return getBuilder(item + addend).parent(HANDHELD).texture(LAYER0, new ResourceLocation(item.getNamespace(), "item/" + item.getPath() + addend));
    }
}
