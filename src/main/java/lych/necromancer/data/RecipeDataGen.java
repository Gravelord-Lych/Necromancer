package lych.necromancer.data;

import lych.necromancer.block.ModBlockGroups;
import lych.necromancer.block.ModBlocks;
import lych.necromancer.item.ModCommonItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class RecipeDataGen extends RecipeProvider {
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
    }

    public static void nineBlockStorageRecipesWithNuggets(RecipeOutput output, RecipeCategory category1, ItemLike nugget, RecipeCategory category2, ItemLike ingot) {
        nineBlockStorageRecipesWithCustomPacking(output, category1, nugget, category2, ingot, "%s_from_nuggets".formatted(getItemName(ingot)), getSimpleRecipeName(ingot));
    }

    public static void stonecutterResultFromBase(RecipeOutput output, RecipeCategory category, ItemLike result, ItemLike ingredient) {
        stonecutterResultFromBase(output, category, result, ingredient, 1);
    }

    public static void stonecutterResultFromBase(RecipeOutput output, RecipeCategory category, ItemLike result, ItemLike ingredient, int count) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), category, result, count).unlockedBy(getHasName(ingredient), has(ingredient)).save(output, getConversionRecipeName(result, ingredient) + "_stonecutting");
    }

    protected void generateForBlockGroups(RecipeOutput output, FeatureFlagSet set) {
        ModBlocks.createFamiliesFromGroup().filter(family -> family.shouldGenerateRecipe(set)).forEach(family -> generateRecipes(output, family));
        ModBlocks.getAllGroups().forEach(group -> group.generateAdditionalRecipes(output));
    }
}
