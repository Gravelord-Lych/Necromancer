package lych.necromancer.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.function.Supplier;

public final class BlockEntry<B extends Block, I extends Item> implements Supplier<B> {
    private final ResourceLocation location;
    private final Supplier<? extends B> block;
    private final Supplier<? extends I> item;
    private final boolean inTab;
    private final boolean usesDefaultBlockItemModel;

    private BlockEntry(ResourceLocation location, Supplier<? extends B> block, Supplier<? extends I> item, boolean inTab, boolean usesDefaultBlockItemModel) {
        this.location = location;
        this.block = block;
        this.item = item;
        this.inTab = inTab;
        this.usesDefaultBlockItemModel = usesDefaultBlockItemModel;
    }

    public static <B extends Block, I extends Item> BlockEntry<B, I> of(ResourceLocation location, Supplier<? extends B> block, Supplier<? extends I> item) {
        return new BlockEntry<>(location, block, item, true, true);
    }

    public BlockEntry<B, I> withCustomBlockItemModel() {
        return new BlockEntry<>(location, block, item, inTab, false);
    }

    public BlockEntry<B, I> noItemInTab() {
        return new BlockEntry<>(location, block, item, false, usesDefaultBlockItemModel);
    }

    public boolean hasItemInTab() {
        return inTab;
    }

    public boolean usesDefaultBlockItemModel() {
        return usesDefaultBlockItemModel;
    }

    @Override
    public B get() {
        return block.get();
    }

    public I item() {
        return item.get();
    }

    public ResourceLocation location() {
        return location;
    }

    @Override
    public String toString() {
        return "{BlockEntry-%s}".formatted(location());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockEntry<?, ?> that = (BlockEntry<?, ?>) o;
        return Objects.equals(location, that.location) && Objects.equals(block, that.block) && Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, block, item);
    }
}
