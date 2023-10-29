package lych.necromancer.data;

import lych.necromancer.Necromancer;
import lych.necromancer.block.BlockGroup;
import lych.necromancer.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static lych.necromancer.block.ModBlocks.NECROITE_BLOCK;

public class BlockTagDataGen extends BlockTagsProvider {
    public BlockTagDataGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Necromancer.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        ModBlocks.getAllGroups().forEach(group -> group.applyCommonTags(this::tag));
        tag(BlockTags.NEEDS_IRON_TOOL).add(NECROITE_BLOCK.get());
    }

    protected void tagAll(TagKey<Block> tagKey, BlockGroup group) {
        tag(tagKey).add(group.all().toArray(Block[]::new));
    }
}
