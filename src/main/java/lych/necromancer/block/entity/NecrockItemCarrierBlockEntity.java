package lych.necromancer.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class NecrockItemCarrierBlockEntity extends BlockEntity implements Clearable {
    private static final String ITEM_INSIDE = "ItemInside";
    private ItemStack itemInside = ItemStack.EMPTY;

    public NecrockItemCarrierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NECROCK_ITEM_CARRIER.get(), pos, state);
    }

    public ItemStack getItemInside() {
        return itemInside;
    }

    public void setItemInside(ItemStack itemInside) {
        this.itemInside = itemInside;
        markUpdated();
    }

    @Override
    public void clearContent() {
        setItemInside(ItemStack.EMPTY);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveItem(tag);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveItem(tag);
    }

    private void saveItem(CompoundTag tag) {
        CompoundTag itemTag = new CompoundTag();
        itemInside.save(itemTag);
        tag.put(ITEM_INSIDE, itemTag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(ITEM_INSIDE, CompoundTag.TAG_COMPOUND)) {
            CompoundTag itemTag = tag.getCompound(ITEM_INSIDE);
            itemInside = ItemStack.of(itemTag);
        }
    }

    public void markUpdated() {
        setChanged();
        if (getLevel() != null) {
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }
}
