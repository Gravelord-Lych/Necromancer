package lych.necromancer.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface ItemCarrier extends Clearable {
    ItemStack getItemInside();

    void setItemInside(ItemStack itemInside);

    void markUpdated();

    @Nullable
    SoundEvent getAddItemSound();

    @Nullable
    SoundEvent getRemoveItemSound();

    default int getMaxCarry() {
        return 1;
    }

    @Override
    default void clearContent() {
        setItemInside(ItemStack.EMPTY);
    }

    default BlockPos getSpawnItemPos(BlockPos myPos) {
        return myPos;
    }

    @Nullable
    static InteractionResult handleInteraction(Level level, BlockPos pos, Player player, InteractionHand hand) {
        if (!(level.getBlockEntity(pos) instanceof ItemCarrier carrier) || hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }

        ItemStack itemInHand = player.getItemInHand(hand);
        ItemStack itemInCarrier = carrier.getItemInside();

        int maxCarry = carrier.getMaxCarry();
        boolean handEmpty = itemInHand.isEmpty();
        boolean carrierEmpty = itemInCarrier.isEmpty();

        if (level.isClientSide()) {
            return !handEmpty && carrierEmpty ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        if (!handEmpty && carrierEmpty) {
            int shrinkCount = Math.min(maxCarry, itemInHand.getCount());
            carrier.setItemInside(itemInHand.copyWithCount(shrinkCount));
            itemInHand.shrink(shrinkCount);

            if (carrier.getAddItemSound() != null) {
                level.playSound(null, pos, carrier.getAddItemSound(), SoundSource.BLOCKS, 1, 1);
            }
            return InteractionResult.CONSUME;
        }

        return null;
    }

    static void handleAttack(Level level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof ItemCarrier carrier)) {
            return;
        }

        ItemStack itemInCarrier = carrier.getItemInside();
        spawnItemAt(level, carrier.getSpawnItemPos(pos), itemInCarrier);
        if (carrier.getRemoveItemSound() != null) {
            level.playSound(null, pos, carrier.getRemoveItemSound(), SoundSource.BLOCKS, 1, 1);
        }
        carrier.setItemInside(ItemStack.EMPTY);
    }

    static void handleExtraDrops(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ItemCarrier carrier) {
            ItemStack itemInCarrier = carrier.getItemInside();
            spawnItemAt(level, carrier.getSpawnItemPos(pos), itemInCarrier);
        }
    }

    private static void spawnItemAt(Level level, BlockPos pos, ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }
}
