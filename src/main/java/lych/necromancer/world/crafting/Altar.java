package lych.necromancer.world.crafting;

import lych.necromancer.block.entity.ModBlockEntities;
import lych.necromancer.block.entity.NecrockItemBaseBlockEntity;
import lych.necromancer.block.entity.NecrockItemCarrierBlockEntity;
import lych.necromancer.util.NonNullListAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Altar implements NecrocraftingContainer {
    private final Level level;
    private final BlockPos basePos;
    private final List<BlockPos> carrierPos;

    public Altar(Level level, BlockPos basePos, List<BlockPos> carrierPos) {
        this.level = level;
        this.basePos = basePos;
        this.carrierPos = List.copyOf(carrierPos);
    }

    @Override
    public ItemStack getBaseItem() {
        return getBaseBlockEntity().map(NecrockItemCarrierBlockEntity::getItemInside).orElse(ItemStack.EMPTY);
    }

    @Override
    public void setBaseItem(ItemStack stack) {
        getBaseBlockEntity().ifPresent(base -> base.setItemInside(stack));
    }

    @Override
    public NonNullList<ItemStack> getCarrierItems() {
        return getCarrierBlockEntities()
                .map(carrier -> carrier.map(NecrockItemCarrierBlockEntity::getItemInside).orElse(ItemStack.EMPTY))
                .collect(NonNullListAccessor.toNonNullList(ItemStack.EMPTY));
    }

    @Override
    public int getCarrierCount() {
        return carrierPos.size();
    }

    @Override
    public void setChanged() {
        getBaseBlockEntity().ifPresent(NecrockItemCarrierBlockEntity::markUpdated);
        getCarrierBlockEntities().flatMap(Optional::stream).forEach(NecrockItemCarrierBlockEntity::markUpdated);
    }

    @Override
    public boolean stillValid(Player player) {
        return validate();
    }

    public boolean validate() {
        return getBaseBlockEntity().isPresent() && getCarrierBlockEntities().allMatch(Optional::isPresent);
    }

    public Optional<AbstractNecrocraftingRecipe> getCraftableRecipe(@Nullable MinecraftServer server) {
        if (server == null) {
            return Optional.empty();
        }
        ItemStack baseItem = getBaseItem();
        return server.getRecipeManager().getRecipeFor(ModRecipeTypes.ORDERED_NECROCRAFTING.get(), this, level).map(RecipeHolder::value);
    }

    public Optional<NecrockItemBaseBlockEntity> getBaseBlockEntity() {
        return level.getBlockEntity(basePos, ModBlockEntities.NECROCK_ITEM_BASE.get());
    }

    public Stream<Optional<NecrockItemCarrierBlockEntity>> getCarrierBlockEntities() {
        return IntStream.range(0, getCarrierCount()).mapToObj(this::getCarrierBlockEntity);
    }

    public Optional<NecrockItemCarrierBlockEntity> getCarrierBlockEntity(int index) {
        return level.getBlockEntity(carrierPos.get(index), ModBlockEntities.NECROCK_ITEM_CARRIER.get());
    }
}
