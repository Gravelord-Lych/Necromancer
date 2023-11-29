package lych.necromancer.block;

import static lych.necromancer.block.BlockGroups.newStoneBlockGroupWithPolishedRef;
import static lych.necromancer.block.ModBlocks.copy;
import static lych.necromancer.block.ModBlocks.using;
import static lych.necromancer.block.BlockGroups.newStoneBlockGroup;

public final class ModBlockGroups {
    public static final StoneBlockGroup NECROCK = newStoneBlockGroupWithPolishedRef(ModBlockNames.NECROCK, using(PropertiesCreator::createNecrockProperties), () -> ModBlockGroups.POLISHED_NECROCK);
    public static final StoneBlockGroup POLISHED_NECROCK = newStoneBlockGroup(ModBlockNames.POLISHED_NECROCK, copy(NECROCK));
    public static final StoneBlockGroup NECROCK_BRICKS = newStoneBlockGroup(ModBlockNames.NECROCK_BRICKS, copy(NECROCK));
    public static final StoneBlockGroup CRACKED_NECROCK_BRICKS = newStoneBlockGroup(ModBlockNames.CRACKED_NECROCK_BRICKS, copy(NECROCK));

    private ModBlockGroups() {}

    public static void init() {}
}
