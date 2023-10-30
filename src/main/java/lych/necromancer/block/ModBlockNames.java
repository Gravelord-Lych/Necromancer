package lych.necromancer.block;

import net.minecraft.resources.ResourceLocation;

public final class ModBlockNames {
    public static final String CRACKED_NECROCK_BRICKS = "cracked_necrock_bricks";
    public static final String NECROCK = "necrock";
    public static final String NECROCK_BRICKS = "necrock_bricks";
    public static final String NECROITE_BLOCK = "necroite_block";
    public static final String POLISHED_NECROCK = "polished_necrock";

    private static final String SLAB_SUFFIX = "_slab";
    private static final String STAIRS_SUFFIX = "_stairs";
    private static final String WALL_SUFFIX = "_wall";

    private ModBlockNames() {}

    public static String slabId(String base) {
        return base + SLAB_SUFFIX;
    }

    public static String stairsId(String base) {
        return base + STAIRS_SUFFIX;
    }

    public static String wallId(String base) {
        return base + WALL_SUFFIX;
    }

    static String wallInventory(ResourceLocation location) {
        return location + "_inventory";
    }
}
