package lych.necromancer.block;

import com.google.common.collect.ImmutableList;
import lych.necromancer.Necromancer;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractBlockGroup implements BlockGroup {
    private final BlockEntry<?, ?> base;

    protected AbstractBlockGroup(BlockEntry<?, ?> base) {
        this.base = base;
    }

    @Override
    public final BlockEntry<?, ?> base() {
        return base;
    }

    @Override
    public final BlockFamily makeFamily() {
        return buildFamily(getBuilder(base.get())).getFamily();
    }

    @Override
    public final List<BlockEntry<?, ?>> allEntries() {
        ImmutableList.Builder<BlockEntry<?, ?>> builder = ImmutableList.builder();
        builder.add(base);
        builder.addAll(getVariants());
        return builder.build();
    }

    @Override
    public void generateBlockStates(BlockStateProvider provider) {
        provider.simpleBlock(base.get());
    }

    @Override
    public void generateAdditionalRecipes(RecipeOutput output) {

    }

    @Override
    public void applyCommonTags(Function<? super TagKey<Block>, ? extends IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>> tagger) {}

    protected BlockFamily.Builder getBuilder(Block base) {
        return new BlockFamily.Builder(base);
    }

    protected <T extends Block> BlockEntry<T, ?> createAndRegisterEntry(String name, Supplier<T> sup) {
        if (Necromancer.MODID.equals(getNamespace())) {
            return ModBlocks.create(name, sup);
        }
        throw new UnsupportedOperationException();
    }

    protected abstract BlockFamily.Builder buildFamily(BlockFamily.Builder builder);

    protected abstract List<BlockEntry<?, ?>> getVariants();

    @Override
    public String toString() {
        return allEntries().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractBlockGroup that = (AbstractBlockGroup) o;
        return Objects.equals(allEntries(), that.allEntries());
    }

    @Override
    public int hashCode() {
        return Objects.hash(allEntries());
    }
}
