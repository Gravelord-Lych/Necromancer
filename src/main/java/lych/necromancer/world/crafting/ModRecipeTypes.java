package lych.necromancer.world.crafting;

import lych.necromancer.Necromancer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Necromancer.MODID);
    public static final RegistryObject<RecipeType<OrderedNecrocraftingRecipe>> ORDERED_NECROCRAFTING = registerSimple(ModRecipeNames.ORDERED_NECROCRAFTING);

    private static RegistryObject<RecipeType<OrderedNecrocraftingRecipe>> registerSimple(String name) {
        return RECIPE_TYPES.register(name, () -> RecipeType.simple(Necromancer.prefix(name)));
    }

    private ModRecipeTypes() {}
}
