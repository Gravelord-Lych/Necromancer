package lych.necromancer.world.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;

public interface NecrocraftingContainer extends Container {
    ItemStack getBaseItem();

    void setBaseItem(ItemStack stack);

    NonNullList<ItemStack> getCarrierItems();

    int getCarrierCount();

    @Override
    default int getContainerSize() {
        return getCarrierCount() + 1;
    }

    @Override
    default ItemStack getItem(int index) {
        if (isPointedAtBase(index)) {
            return getBaseItem();
        }
        return getCarrierItems().get(index);
    }

    @Override
    default ItemStack removeItem(int index, int count) {
        if (isPointedAtBase(index)) {
            return ContainerHelper.removeItem(Collections.singletonList(getBaseItem()), 0, count);
        }
        return ContainerHelper.removeItem(getCarrierItems(), index, count);
    }

    @Override
    default ItemStack removeItemNoUpdate(int index) {
        if (isPointedAtBase(index)) {
            ItemStack baseItem = getBaseItem().copy();
            setBaseItem(ItemStack.EMPTY);
            return baseItem;
        }
        return getCarrierItems().set(index, ItemStack.EMPTY);
    }

    @Override
    default void setItem(int index, ItemStack stack) {
        getCarrierItems().set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
    }

    @Override
    default void clearContent() {
        getCarrierItems().clear();
        setBaseItem(ItemStack.EMPTY);
    }

    @Override
    default boolean isEmpty() {
        return getBaseItem().isEmpty() && getCarrierItems().isEmpty();
    }

    private boolean isPointedAtBase(int index) {
        return index == getCarrierCount();
    }
}
