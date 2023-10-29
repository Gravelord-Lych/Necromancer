package lych.necromancer.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static lych.necromancer.block.ModBlocks.copy;
import static lych.necromancer.block.ModBlocks.using;
import static lych.necromancer.block.BlockGroups.newStoneBlockGroup;

public final class ModBlockGroups {
    public static final StoneBlockGroup NECROCK = newStoneBlockGroup(ModBlockNames.NECROCK, using(p -> p.mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.5F, 6).sound(SoundType.STONE)));
    public static final StoneBlockGroup NECROCK_BRICKS = newStoneBlockGroup(ModBlockNames.NECROCK_BRICKS, copy(NECROCK));
    public static final StoneBlockGroup CRACKED_NECROCK_BRICKS = newStoneBlockGroup(ModBlockNames.CRACKED_NECROCK_BRICKS, copy(NECROCK));

    private ModBlockGroups() {}

    public static void init() {}
}
