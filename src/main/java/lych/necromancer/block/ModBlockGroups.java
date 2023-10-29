package lych.necromancer.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static lych.necromancer.block.ModBlocks.using;
import static lych.necromancer.block.BlockGroups.newStoneBlockGroup;

public final class ModBlockGroups {
    public static final StoneBlockGroup NECROCK = newStoneBlockGroup(ModBlockNames.NECROCK, using(p -> p.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.5F, 6).sound(SoundType.STONE)));

    private ModBlockGroups() {}

    public static void init() {}
}
