package lych.necromancer.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class PropertiesCreator {
    private PropertiesCreator() {}

    public static BlockBehaviour.Properties createNecrockProperties(BlockBehaviour.Properties p) {
        return p.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.5F, 6).sound(SoundType.STONE);
    }
}
