package lych.necromancer.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lych.necromancer.world.crafting.ModRecipeSerializers;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class OrderedNecrocraftingRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final Ingredient baseIngredient;
    private final List<Ingredient> carrierIngredients = new ArrayList<>();
    private final int count;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;
    @Nullable
    private ICondition condition;
    private int energyCost;
    private final RecipeSerializer<?> type;

    public OrderedNecrocraftingRecipeBuilder(RecipeCategory category, RecipeSerializer<?> type, Ingredient baseIngredient, ItemLike result, int count) {
        this.category = category;
        this.type = type;
        this.result = result.asItem();
        this.baseIngredient = baseIngredient;
        this.count = count;
    }

    public static OrderedNecrocraftingRecipeBuilder ordered(Ingredient ingredient, RecipeCategory category, ItemLike result) {
        return ordered(ingredient, category, result, 1);
    }

    public static OrderedNecrocraftingRecipeBuilder ordered(Ingredient baseIngredient, RecipeCategory category, ItemLike result, int count) {
        return new OrderedNecrocraftingRecipeBuilder(category, ModRecipeSerializers.ORDERED_NECROCRAFTING.get(), baseIngredient, result, count);
    }

    public OrderedNecrocraftingRecipeBuilder cycleCarrierIngredients(int count, Ingredient... ingredients) {
        for (int i = 0; i < count; i++) {
            carrierIngredients(ingredients);
        }
        return this;
    }

    public OrderedNecrocraftingRecipeBuilder carrierIngredients(Ingredient... ingredients) {
        carrierIngredients.addAll(List.of(ingredients));
        return this;
    }

    public OrderedNecrocraftingRecipeBuilder energyCost(int energyCost) {
        this.energyCost = energyCost;
        return this;
    }

    @Override
    public OrderedNecrocraftingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        criteria.put(name, criterion);
        return this;
    }

    @Override
    public OrderedNecrocraftingRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    public OrderedNecrocraftingRecipeBuilder condition(ICondition condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public Item getResult() {
        return result;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation location) {
        ensureValid(location);
        Advancement.Builder builder = output.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(location)).rewards(AdvancementRewards.Builder.recipe(location)).requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(builder::addCriterion);
        output.accept(new Result(location, type, group == null ? "" : group, baseIngredient, carrierIngredients, result, count, energyCost, builder.build(location.withPrefix("recipes/" + category.getFolderName() + "/")), condition));
    }

    private void ensureValid(ResourceLocation location) {
        if (criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + location);
        }
    }

    public record Result(ResourceLocation id, RecipeSerializer<?> type, String group, Ingredient baseIngredient, List<Ingredient> carrierIngredients, Item result, int count, int energyCost, AdvancementHolder advancement, @Nullable ICondition condition) implements FinishedRecipe {
        public Result(ResourceLocation id, RecipeSerializer<?> type, String group, Ingredient baseIngredient, List<Ingredient> carrierIngredients, Item result, int count, int energyCost, AdvancementHolder advancement) {
            this(id, type, group, baseIngredient, carrierIngredients, result, count, energyCost, advancement, null);
        }

        @SuppressWarnings("UnstableApiUsage")
        @Override
        public void serializeRecipeData(JsonObject object) {
            ForgeHooks.writeCondition(condition, object);
            if (!group.isEmpty()) {
                object.addProperty("group", group);
            }

            object.add("base_ingredient", baseIngredient.toJson(false));

            JsonArray carrierIngredientsArray = new JsonArray();
            carrierIngredients.stream().map(ingredient -> ingredient.toJson(false)).forEach(carrierIngredientsArray::add);

            object.add("carrier_ingredients", carrierIngredientsArray);
            object.addProperty("result", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result)).toString());
            object.addProperty("dp", energyCost);
            object.addProperty("count", count);
        }

        @Override
        public ResourceLocation id() {
            return id;
        }

        @Override
        public RecipeSerializer<?> type() {
            return type;
        }

        @Override
        public AdvancementHolder advancement() {
            return advancement;
        }
    }
}
