package lych.necromancer.world.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

public class UnorderedNecrocraftingRecipe extends AbstractNecrocraftingRecipe {
    public UnorderedNecrocraftingRecipe(Ingredient baseIngredient, List<Ingredient> carrierIngredients, ItemStack result, int energyCost) {
        super(baseIngredient, carrierIngredients, result, energyCost);
    }

    @Override
    public boolean matches(NecrocraftingContainer container, Level level) {
        if (baseIngredient.test(container.getBaseItem())) {
            List<ItemStack> carrierItems = new ArrayList<>(container.getCarrierItems());
            carrierItems.removeIf(ItemStack::isEmpty);
            return RecipeMatcher.findMatches(carrierItems, carrierIngredients) != null;
        }
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.UNORDERED_NECROCRAFTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.UNORDERED_NECROCRAFTING.get();
    }

    public static class Serializer extends BaseSerializer<UnorderedNecrocraftingRecipe> {
        @Override
        protected UnorderedNecrocraftingRecipe make(Ingredient baseIngredient, List<Ingredient> carrierIngredients, ItemStack result, int energyCost) {
            return new UnorderedNecrocraftingRecipe(baseIngredient, carrierIngredients, result, energyCost);
        }
    }
}
