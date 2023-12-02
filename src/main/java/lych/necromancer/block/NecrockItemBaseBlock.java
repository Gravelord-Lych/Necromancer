package lych.necromancer.block;

import lych.necromancer.block.entity.ModBlockEntities;
import lych.necromancer.block.entity.NecrockItemBaseBlockEntity;
import lych.necromancer.capability.IDarkPowerStorage;
import lych.necromancer.item.ModSpecialItems;
import lych.necromancer.world.crafting.AbstractNecrocraftingRecipe;
import lych.necromancer.world.crafting.Altar;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;
import java.util.Optional;

public final class NecrockItemBaseBlock extends NecrockItemCarrierBlock {
    private static final VoxelShape SHAPE = Shapes.or(Block.box(3, 0, 3, 13, 2, 13), Block.box(6, 0, 6, 10, 10, 10), Block.box(4, 10, 4, 12, 12, 12));

    public NecrockItemBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }
        ItemStack wand = player.getMainHandItem();
        if (wand.getItem() == ModSpecialItems.NECROWAND.get()) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            } else {
                var optionalBase = level.getBlockEntity(pos, ModBlockEntities.NECROCK_ITEM_BASE.get());
                var optionalRecipe = optionalBase.map(NecrockItemBaseBlockEntity::getCraftableRecipe);

                if (optionalRecipe.isPresent() && optionalRecipe.get().isPresent()) {
                    NecrockItemBaseBlockEntity base = optionalBase.get();
                    AbstractNecrocraftingRecipe recipe = optionalRecipe.get().get();

                    int energyCost = recipe.getEnergyCost();
                    IDarkPowerStorage dps = IDarkPowerStorage.of(wand);

                    if (dps.extractDarkPower(energyCost)) {
                        Altar altar = Objects.requireNonNull(base.getAltar());

                        base.setItemInside(recipe.assemble(altar, level.registryAccess()));
                        altar.getCarrierBlockEntities()
                                .flatMap(Optional::stream)
                                .forEach(carrier -> carrier.setItemInside(carrier.getItemInside().getCraftingRemainingItem()));

                        return InteractionResult.CONSUME;
                    }
                }
            }
        }
        return super.use(state, level, pos, player, hand, result);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NecrockItemBaseBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.NECROCK_ITEM_BASE.get(), level.isClientSide() ? NecrockItemBaseBlockEntity::clientTick : NecrockItemBaseBlockEntity::serverTick);
    }
}
