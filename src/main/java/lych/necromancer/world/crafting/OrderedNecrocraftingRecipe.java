package lych.necromancer.world.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class OrderedNecrocraftingRecipe extends AbstractNecrocraftingRecipe {
    public OrderedNecrocraftingRecipe(Ingredient baseIngredient, List<Ingredient> carrierIngredients, ItemStack result, int energyCost) {
        super(baseIngredient, carrierIngredients, result, energyCost);
    }

    @Override
    public boolean matches(NecrocraftingContainer container, Level level) {
        if (baseIngredient.test(container.getBaseItem())) {
            for (int i = 0; i < container.getCarrierCount(); i++) {
                boolean ok = true;
                for (int j = 0; j < container.getCarrierCount(); j++) {
                    int itemIndex = (i + j) % container.getCarrierCount();
                    if (!carrierIngredients.get(j).test(container.getItem(itemIndex))) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.ORDERED_NECROCRAFTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ORDERED_NECROCRAFTING.get();
    }

    public static class Serializer extends BaseSerializer<OrderedNecrocraftingRecipe> {
        @Override
        protected OrderedNecrocraftingRecipe make(Ingredient baseIngredient, List<Ingredient> carrierIngredients, ItemStack result, int energyCost) {
            return new OrderedNecrocraftingRecipe(baseIngredient, carrierIngredients, result, energyCost);
        }
    }
}
