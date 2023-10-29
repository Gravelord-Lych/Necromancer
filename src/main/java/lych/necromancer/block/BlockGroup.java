package lych.necromancer.block;

import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface BlockGroup extends Supplier<Block> {
    BlockEntry<?, ?> base();

    List<BlockEntry<?, ?>> allEntries();

    BlockFamily makeFamily();

    void generateBlockStates(BlockStateProvider provider);

    void generateAdditionalRecipes(RecipeOutput output);

    void applyCommonTags(Function<? super TagKey<Block>, ? extends IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>> tagger);

    default boolean supportsBlockFamily() {
        return true;
    }

    default String getNamespace() {
        return baseLocation().getNamespace();
    }

    default ResourceLocation baseLocation() {
        return base().location();
    }

    default ResourceLocation prefixedLocation() {
        return new ResourceLocation(baseLocation().getNamespace(), "block/" + baseLocation().getPath());
    }

    default void forAllEntries(Consumer<? super BlockEntry<?, ?>> action) {
        allEntries().forEach(action);
    }

    default Stream<Block> all() {
        return allEntries().stream().map(BlockEntry::get);
    }

    @Override
    default Block get() {
        return base().get();
    }
}
