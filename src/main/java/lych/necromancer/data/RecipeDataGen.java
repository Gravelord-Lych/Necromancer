package lych.necromancer.data;

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
