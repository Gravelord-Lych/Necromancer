package lych.necromancer.block;

import lych.necromancer.block.entity.NecrockItemCarrierBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class NecrockItemCarrierBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE = Shapes.or(Block.box(1, 0, 1, 15, 2, 15), Block.box(2, 0, 2, 14, 3, 14));
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public NecrockItemCarrierBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState myState, Direction direction, BlockState otherState, LevelAccessor level, BlockPos myPos, BlockPos otherPos) {
        if (myState.getValue(WATERLOGGED)) {
            level.scheduleTick(myPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(myState, direction, otherState, level, myPos, otherPos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!(level.getBlockEntity(pos) instanceof NecrockItemCarrierBlockEntity carrier) || hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }

        ItemStack itemInHand = player.getItemInHand(hand);
        ItemStack itemInCarrier = carrier.getItemInside();

        boolean handEmpty = itemInHand.isEmpty();
        boolean carrierEmpty = itemInCarrier.isEmpty();

        if (level.isClientSide()) {
            return !handEmpty || !carrierEmpty ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        if (!handEmpty && carrierEmpty) {
            carrier.setItemInside(itemInHand.copyAndClear());
            return InteractionResult.CONSUME;
        }
        if (handEmpty && !carrierEmpty) {
            player.setItemInHand(hand, itemInCarrier.copyAndClear());
            carrier.markUpdated();
            return InteractionResult.CONSUME;
        }
        if (!handEmpty && !carrierEmpty) {
            ItemStack itemInHandCopy = itemInHand.copy();
            player.setItemInHand(hand, itemInCarrier.copyAndClear());
            carrier.setItemInside(itemInHandCopy);
            return InteractionResult.CONSUME;
        }

        return super.use(state, level, pos, player, hand, result);
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NecrockItemCarrierBlockEntity(pos, state);
    }
}
