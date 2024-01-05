package lych.necromancer.world.event;

import lych.necromancer.world.event.round.Round;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class SpawnPositionProvider {
    protected static final int MAX_ATTEMPT = 20;
    protected static final int MAX_MOVE = 40;
    protected final AbstractOnslaught<?, ?> onslaught;
    protected final double minRange;
    protected final double maxRange;

    public SpawnPositionProvider(AbstractOnslaught<?, ?> onslaught, double minRange, double maxRange) {
        this.onslaught = onslaught;
        this.minRange = Math.min(minRange, maxRange);
        this.maxRange = Math.max(minRange, maxRange);
    }

    public Vec3 getMobPosition(Round<?> round) {
        Vec3 vec3;
        AdjustedPosition adjusted;

        int attempts = 0;
        do {
            attempts++;
            vec3 = generate(round);
            adjusted = adjust(vec3, round);
        } while (attempts < MAX_ATTEMPT && !adjusted.satisfactory());

        return adjusted.pos();
    }

    protected Vec3 generate(Round<?> round) {
        BlockPos center = onslaught.getCenter();
        double range = onslaught.level().random.nextDouble() * (maxRange - minRange) + minRange;
        float angle = (float) (onslaught.level().random.nextDouble() * 2 * Math.PI);
        double x = Mth.cos(angle) * range * round.getRangeModifier();
        double z = Mth.sin(angle) * range * round.getRangeModifier();
        return new Vec3(center.getX() + x, center.getY(), center.getZ() + z);
    }

    protected AdjustedPosition adjust(Vec3 vec3, Round<?> round) {
        ServerLevel level = onslaught.level();
        double maxHeight = round.getHeightRequirement();
        BlockPos adjusted = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, BlockPos.containing(vec3));
        return AdjustedPosition.construct(vec3, adjusted, true);
    }

    protected final boolean isInvalidPos(Vec3 vec3, double maxHeight, BlockPos adjusted) {
        return isPosSuspicious(vec3, adjusted) || isPosBlocked(adjusted, maxHeight);
    }

    //  Maybe the position is a trap for our monsters...
    protected boolean isPosSuspicious(Vec3 old, BlockPos adjusted) {
        return Math.abs(old.y() - adjusted.getY()) > 10;
    }

    protected boolean isPosBlocked(BlockPos adjusted, double heightRequirement) {
        int minHeight = Mth.ceil(heightRequirement);
        for (int i = 0; i < minHeight; i++) {
            if (isSolid(adjusted.above(i))) {
                return true;
            }
        }
        return false;
    }

    protected BlockPos moveUpManually(Vec3 old) {
        BlockPos.MutableBlockPos mutable = BlockPos.containing(old).mutable();
        int counter = 0;
        while (isSolid(mutable) && counter < MAX_MOVE) {
            mutable.move(Direction.UP);
            counter++;
        }
        return mutable.immutable();
    }

    protected BlockPos moveDownManually(Vec3 old) {
        BlockPos.MutableBlockPos mutable = BlockPos.containing(old).mutable();
        int counter = 0;
        while (!isSolid(mutable) && counter < MAX_MOVE) {
            mutable.move(Direction.DOWN);
            counter++;
        }
        mutable.move(Direction.UP);
        return mutable.immutable();
    }

    @SuppressWarnings("deprecation")
    protected boolean isSolid(BlockPos pos) {
        return onslaught.level().getBlockState(pos).isSolid();
    }

    protected record AdjustedPosition(Vec3 pos, boolean satisfactory) {
        public static AdjustedPosition construct(Vec3 vec3, BlockPos adjusted, boolean satisfactory) {
            return new AdjustedPosition(new Vec3(vec3.x(), adjusted.getY(), vec3.z()), satisfactory);
        }
    }
}
