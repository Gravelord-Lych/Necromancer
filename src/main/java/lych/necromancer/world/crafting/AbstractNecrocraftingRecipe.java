package lych.necromancer.world.crafting;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.necromancer.capability.IDarkPowerStorage;
import lych.necromancer.capability.ModCapabilities;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNecrocraftingRecipe implements Recipe<NecrocraftingContainer> {
    protected final ItemStack result;
    protected final Ingredient baseIngredient;
    protected final List<Ingredient> carrierIngredients;
    protected final int energyCost;

    protected AbstractNecrocraftingRecipe(Ingredient baseIngredient, List<Ingredient> carrierIngredients, ItemStack result, int energyCost) {
        this.result = result;
        this.baseIngredient = baseIngredient;
        this.carrierIngredients = carrierIngredients;
        this.energyCost = energyCost;
    }

    public boolean matches(NecrocraftingContainer container, Level level, ItemStack necrowand) {
        return necrowand.getCapability(ModCapabilities.DARK_POWER_STORAGE).map(IDarkPowerStorage::getDarkPower).orElse(0) >= energyCost && matches(container, level);
    }

    @Override
    public abstract boolean matches(NecrocraftingContainer container, Level level);

    @Override
    public ItemStack assemble(NecrocraftingContainer container, RegistryAccess access) {
        return getResultItem(access).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return result;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public static abstract class BaseSerializer<T extends AbstractNecrocraftingRecipe> implements RecipeSerializer<T> {
        private final Codec<T> codec = RecordCodecBuilder.create(this::makeCodec);

        private App<RecordCodecBuilder.Mu<T>, T> makeCodec(RecordCodecBuilder.Instance<T> instance) {
            Codec<List<Ingredient>> carrierIngredientsCodec = Codec.list(Ingredient.CODEC);
            Codec<ItemStack> resultCodec = ForgeRegistries.ITEMS.getCodec().xmap(ItemStack::new, ItemStack::getItem);

            return instance.group(Ingredient.CODEC.fieldOf("base_ingredient").forGetter(recipe -> recipe.baseIngredient),
                    carrierIngredientsCodec.fieldOf("carrier_ingredients").forGetter(recipe -> recipe.carrierIngredients),
                    resultCodec.fieldOf("result").forGetter(recipe -> recipe.result),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("dp").forGetter(recipe -> recipe.energyCost)).apply(instance, this::make);
        }

        protected abstract T make(Ingredient baseIngredient, List<Ingredient> carrierIngredients, ItemStack result, int energyCost);

        @Override
        public Codec<T> codec() {
            return codec;
        }

        @Override
        public T fromNetwork(FriendlyByteBuf buf) {
            Ingredient baseIngredient = Ingredient.fromNetwork(buf);
            List<Ingredient> carrierIngredients = buf.readCollection(ArrayList::new, Ingredient::fromNetwork);
            ItemStack result = buf.readItem();
            int energyCost = buf.readVarInt();
            return make(baseIngredient, carrierIngredients, result, energyCost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, T recipe) {
            recipe.baseIngredient.toNetwork(buf);
            buf.writeCollection(recipe.carrierIngredients, (bufIn, ingredient) -> ingredient.toNetwork(bufIn));
            buf.writeItem(recipe.result);
            buf.writeVarInt(recipe.energyCost);
        }
    }
}
