package lych.necromancer.data;

import lych.necromancer.Necromancer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static lych.necromancer.item.ModCommonItems.NECRODUST;
import static lych.necromancer.item.ModCommonItems.NECROITE_INGOT;

public class ItemTagDataGen extends ItemTagsProvider {
    public ItemTagDataGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, CompletableFuture<TagLookup<Block>> blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, registries, blockTagProvider, Necromancer.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(Tags.Items.DUSTS).add(NECRODUST.get());
        tag(Tags.Items.INGOTS).add(NECROITE_INGOT.get());
    }
}
