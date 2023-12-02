package lych.necromancer.world.crafting;

import lych.necromancer.Necromancer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Necromancer.MODID);
    public static final RegistryObject<RecipeSerializer<OrderedNecrocraftingRecipe>> ORDERED_NECROCRAFTING =
            RECIPE_SERIALIZERS.register(ModRecipeNames.ORDERED_NECROCRAFTING, OrderedNecrocraftingRecipe.Serializer::new);

    private ModRecipeSerializers() {}
}
