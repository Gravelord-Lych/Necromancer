package lych.necromancer.data;

import lych.necromancer.Necromancer;
import lych.necromancer.block.BlockEntry;
import lych.necromancer.block.BlockGroup;
import lych.necromancer.block.ModBlockGroups;
import lych.necromancer.block.ModBlocks;
import lych.necromancer.util.tab.ReflectionUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

import static lych.necromancer.item.ModCommonItems.NECRODUST;
import static lych.necromancer.item.ModCommonItems.NECROITE_INGOT;

public class ItemModelDataGen extends ItemModelProvider {
    public ItemModelDataGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Necromancer.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(NECRODUST.get());
        basicItem(NECROITE_INGOT.get());

        ReflectionUtils.iterateFields(ModBlockGroups.class, BlockGroup.class, field -> ((BlockGroup) field.get(null)).forAllEntries(this::defaultBlockItem));
        ReflectionUtils.iterateFields(ModBlocks.class, BlockEntry.class, field -> defaultBlockItem((BlockEntry<?, ?>) field.get(null)));
    }

    private void defaultBlockItem(BlockEntry<?, ?> entry) {
        if (entry.usesDefaultBlockItemModel()) {
            ResourceLocation blockLocation = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(entry.get()));
            getBuilder(entry.item().toString()).parent(new ModelFile.UncheckedModelFile(getPath(entry, blockLocation)));
        }
    }

    private static String getPath(BlockEntry<?, ?> entry, ResourceLocation blockLocation) {
        StringBuilder res = new StringBuilder().append(blockLocation.getNamespace()).append(":block/").append(blockLocation.getPath());
        if (entry.get() instanceof WallBlock || entry.get() instanceof FenceBlock) {
            res.append("_inventory");
        }
        return res.toString();
    }
}
