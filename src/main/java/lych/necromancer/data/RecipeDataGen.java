package lych.necromancer.data;

import com.google.common.collect.ImmutableMap;
import lych.necromancer.Necromancer;
import lych.necromancer.block.ModBlockGroups;
import lych.necromancer.block.ModBlocks;
import lych.necromancer.data.recipes.OrderedNecrocraftingRecipeBuilder;
import lych.necromancer.item.ModCommonItems;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiFunction;

public class RecipeDataGen extends RecipeProvider {
    /**
     * [VanillaCopy]
     */
    private static final Map<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> SHAPE_BUILDERS = ImmutableMap.<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>>builder()
            .put(BlockFamily.Variant.BUTTON, (result, ingredient) -> buttonBuilder(result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.CHISELED, (result, ingredient) -> chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.CUT, (result, ingredient) -> cutBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.DOOR, (result, ingredient) -> doorBuilder(result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.CUSTOM_FENCE, (result, ingredient) -> fenceBuilder(result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.FENCE, (result, ingredient) -> fenceBuilder(result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.CUSTOM_FENCE_GATE, (result, ingredient) -> fenceGateBuilder(result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.FENCE_GATE, (result, ingredient) -> fenceGateBuilder(result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.SIGN, (result, ingredient) -> signBuilder(result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.SLAB, (result, ingredient) -> slabBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.STAIRS, (result, ingredient) -> stairBuilder(result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.PRESSURE_PLATE, (result, ingredient) -> pressurePlateBuilder(RecipeCategory.REDSTONE, result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.POLISHED, (result, ingredient) -> polishedBuilder(RecipeCategory.BUILDING_BLOCKS, result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.TRAPDOOR, (result, ingredient) -> trapdoorBuilder(result, Ingredient.of(ingredient)))
            .put(BlockFamily.Variant.WALL, (result, ingredient) -> wallBuilder(RecipeCategory.DECORATIONS, result, Ingredient.of(ingredient)))
            .build();

    public RecipeDataGen(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        generateForBlockGroups(output, FeatureFlagSet.of(FeatureFlags.VANILLA));
        nineBlockStorageRecipes(output, RecipeCategory.MISC, ModCommonItems.NECROITE_INGOT.get(), RecipeCategory.BUILDING_BLOCKS, ModBlocks.NECROITE_BLOCK.get());
        nineBlockStorageRecipesWithNuggets(output, RecipeCategory.MISC, ModCommonItems.NECROITE_NUGGET.get(), RecipeCategory.MISC, ModCommonItems.NECROITE_INGOT.get());
        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, ModBlockGroups.NECROCK_BRICKS.base().get(), ModBlockGroups.NECROCK.base().get());
        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, ModBlockGroups.NECROCK_BRICKS.slab().get(), ModBlockGroups.NECROCK.base().get(), 2);
        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, ModBlockGroups.NECROCK_BRICKS.stairs().get(), ModBlockGroups.NECROCK.base().get());
        OrderedNecrocraftingRecipeBuilder.ordered(Ingredient.of(Items.COAL), RecipeCategory.MISC, Items.DIAMOND)
                .cycleCarrierIngredients(4, Ingredient.of(Items.IRON_NUGGET), Ingredient.of(Items.GOLD_NUGGET))
                .energyCost(100)
                .unlockedBy(getHasName(Items.COAL), has(Items.COAL))
                .save(output, Necromancer.prefix("test_recipe_of_ordered_necrocrafting"));
    }

    protected static void nineBlockStorageRecipes(RecipeOutput output, RecipeCategory category, ItemLike small, RecipeCategory smallToBigCategory, ItemLike big) {
        nineBlockStorageRecipes(output, category, small, smallToBigCategory, big, getSimpleRecipeName(big), null, getSimpleRecipeName(small), null);
    }

    protected static void nineBlockStorageRecipesWithCustomPacking(RecipeOutput output, RecipeCategory bigToSmallCategory, ItemLike small, RecipeCategory smallToBigCategory, ItemLike big, String smallName, String smallToBigGroup) {
        nineBlockStorageRecipes(output, bigToSmallCategory, small, smallToBigCategory, big, smallName, smallToBigGroup, getSimpleRecipeName(small), null);
    }

    protected static void nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeOutput output, RecipeCategory bigToSmallCategory, ItemLike small, RecipeCategory smallToBigCategory, ItemLike big, String smallName, String bigToSmallGroup) {
        nineBlockStorageRecipes(output, bigToSmallCategory, small, smallToBigCategory, big, getSimpleRecipeName(big), null, smallName, bigToSmallGroup);
    }

    private static void nineBlockStorageRecipes(RecipeOutput output, RecipeCategory bigToSmallCategory, ItemLike small, RecipeCategory smallToBigCategory, ItemLike big, String bigName, @Nullable String smallToBigGroup, String smallName, @Nullable String bigToSmallGroup) {
        ShapelessRecipeBuilder.shapeless(bigToSmallCategory, small, 9).requires(big).group(bigToSmallGroup).unlockedBy(getHasName(big), has(big)).save(output, Necromancer.prefix(smallName));
        ShapedRecipeBuilder.shaped(smallToBigCategory, big).define('#', small).pattern("###").pattern("###").pattern("###").group(smallToBigGroup).unlockedBy(getHasName(small), has(small)).save(output, Necromancer.prefix(bigName));
    }

    public static void nineBlockStorageRecipesWithNuggets(RecipeOutput output, RecipeCategory category1, ItemLike nugget, RecipeCategory category2, ItemLike ingot) {
        nineBlockStorageRecipesWithCustomPacking(output, category1, nugget, category2, ingot, "%s_from_nuggets".formatted(getItemName(ingot)), getSimpleRecipeName(ingot));
    }

    public static void stonecutterResultFromBase(RecipeOutput output, RecipeCategory category, ItemLike result, ItemLike ingredient) {
        stonecutterResultFromBase(output, category, result, ingredient, 1);
    }

    public static void stonecutterResultFromBase(RecipeOutput output, RecipeCategory category, ItemLike result, ItemLike ingredient, int count) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), category, result, count).unlockedBy(getHasName(ingredient), has(ingredient)).save(output, Necromancer.prefix(getConversionRecipeName(result, ingredient) + "_stonecutting"));
    }

    private static void smeltingResultFromBase(RecipeOutput output, ItemLike result, ItemLike ingredient) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), RecipeCategory.BUILDING_BLOCKS, result, 0.1F, 200).unlockedBy(getHasName(ingredient), has(ingredient)).save(output);
    }

    protected void generateForBlockGroups(RecipeOutput output, FeatureFlagSet set) {
        ModBlocks.createFamiliesFromGroup().filter(family -> family.shouldGenerateRecipe(set)).forEach(family -> generateRecipes(output, family));
        ModBlocks.getAllGroups().forEach(group -> group.generateAdditionalRecipes(output));
    }

    protected static void generateRecipes(RecipeOutput output, BlockFamily family) {
        family.getVariants().forEach((variant, block) -> {
            BiFunction<ItemLike, ItemLike, RecipeBuilder> builderFunction = SHAPE_BUILDERS.get(variant);
            ItemLike baseBlock = getBaseBlock(family, variant);
            if (builderFunction != null) {
                RecipeBuilder builder = builderFunction.apply(block, baseBlock);
                family.getRecipeGroupPrefix().ifPresent(prefix -> builder.group(prefix + (variant == BlockFamily.Variant.CUT ? "" : "_" + variant.getRecipeGroup())));
                builder.unlockedBy(family.getRecipeUnlockedBy().orElseGet(() -> getHasName(baseBlock)), has(baseBlock));
                builder.save(output);
            }

            if (variant == BlockFamily.Variant.CRACKED) {
                RecipeDataGen.smeltingResultFromBase(output, block, baseBlock);
            }
        });
    }
}
