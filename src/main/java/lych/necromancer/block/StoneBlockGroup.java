package lych.necromancer.block;

import lych.necromancer.data.RecipeDataGen;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class StoneBlockGroup extends AbstractBlockGroup {
    private final BlockEntry<? extends SlabBlock, ?> slab;
    private final BlockEntry<? extends StairBlock, ?> stairs;
    private final BlockEntry<? extends WallBlock, ?> wall;

    protected StoneBlockGroup(BlockEntry<?, ?> base) {
        super(base);

        Supplier<SlabBlock> slabBlockSup = () -> new SlabBlock(Properties.copy(base.get()));
        slab = createAndRegisterEntry(ModBlockNames.slabId(baseLocation().getPath()), slabBlockSup);

        Supplier<StairBlock> stairBlockSup = () -> new StairBlock(() -> base.get().defaultBlockState(), Properties.copy(base.get()));
        stairs = createAndRegisterEntry(ModBlockNames.stairsId(baseLocation().getPath()), stairBlockSup);

        Supplier<WallBlock> wallBlockSup = () -> new WallBlock(Properties.copy(base.get()));
        wall = createAndRegisterEntry(ModBlockNames.wallId(baseLocation().getPath()), wallBlockSup);
    }

    public static StoneBlockGroup of(BlockEntry<?, ?> base) {
        return new StoneBlockGroup(base);
    }

    @Override
    protected BlockFamily.Builder buildFamily(BlockFamily.Builder builder) {
        return builder.slab(slab.get()).stairs(stairs.get()).wall(wall.get());
    }

    @Override
    protected List<BlockEntry<?, ?>> getVariants() {
        return List.of(slab, stairs, wall);
    }

    @Override
    public void applyCommonTags(Function<? super TagKey<Block>, ? extends IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>> tagger) {
        tagger.apply(BlockTags.SLABS).add(slab.get());
        tagger.apply(BlockTags.STAIRS).add(stairs.get());
        tagger.apply(BlockTags.WALLS).add(wall.get());
        tagger.apply(Tags.Blocks.STONE).add(base().get());
        forAllEntries(entry -> tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(entry.get()));
    }

    @Override
    public void generateBlockStates(BlockStateProvider provider) {
        super.generateBlockStates(provider);
        provider.slabBlock(slab.get(), prefixedLocation(), prefixedLocation());
        provider.stairsBlock(stairs.get(), prefixedLocation());
        provider.wallBlock(wall.get(), prefixedLocation());
        provider.models().wallInventory(ModBlockNames.wallInventory(wall.location()), prefixedLocation());
    }

    @Override
    public void generateAdditionalRecipes(RecipeOutput output) {
        RecipeDataGen.stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, slab.get(), base().get(), 2);
        RecipeDataGen.stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, stairs.get(), base().get());
        RecipeDataGen.stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, wall.get(), base().get());
    }

    public BlockEntry<? extends SlabBlock, ?> slab() {
        return slab;
    }

    public BlockEntry<? extends StairBlock, ?> stairs() {
        return stairs;
    }

    public BlockEntry<? extends WallBlock, ?> wall() {
        return wall;
    }
}
