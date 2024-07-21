package lych.necromancer.world.level;

import com.google.common.collect.*;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import lych.necromancer.mixin.MapItemSavedDataAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import static it.unimi.dsi.fastutil.ints.IntIntImmutablePair.of;

public class EnderHighlighter {
    public static final Set<IntIntPair> SPECIAL_POSITIONS = ImmutableSet.of(of(60,0),
            of(51,30),
            of(29,51),
            of(0,60),
            of(-31,51),
            of(-52,29),
            of(-60,0),
            of(-52,-31),
            of(-30,-52),
            of(0,-60),
            of(29,-52),
            of(51,-30),
            of(40,0),
            of(28,28),
            of(0,40),
            of(-29,28),
            of(-40,0),
            of(-29,-29),
            of(0,-40),
            of(28,-29),
            of(20,0),
            of(0,20),
            of(-20,0),
            of(0,-20));
    public static final int r = 256;
    public static final int r_sqr = r * r;
    private static final boolean DO_NOT_DRAW = true;
    private static boolean drawn = false;

    public static void draw(ServerLevel level, Player player) {
        if (DO_NOT_DRAW || drawn) {
            return;
        }
        ItemStack stack = MapItem.create(level, 0, 0, (byte) 0, false, false);
        MapItemSavedData data = update(level, player, MapItem.getSavedData(stack, level));

        toImage(data, "ender", false);
        toImage(data, "ender_marked", true);

        drawn = true;
    }

    private static void toImage(MapItemSavedData data, String name, boolean markSpecialPositions) {
        BufferedImage image = newImage();

        if (markSpecialPositions) {
            markSpecialPositions(image);
        } else {
            drawTerrain(data, image);
        }
        save(name, image);
    }

    public static void drawTerrain(MapItemSavedData data, BufferedImage image) {
        for (int i = 0; i < r; ++i) {
            for (int j = 0; j < r; ++j) {
                int x = j - r / 2;
                int z = i - r / 2;
                int k = j + i * r;
                int colorFromPackedId = MapColor.getColorFromPackedId(data.colors[k]);
                image.setRGB(j, i, adapt(colorFromPackedId));
            }
        }
    }

    public static void markSpecialPositions(BufferedImage image) {
        image.setRGB(r / 2, r / 2, 0x00FFFF);
        for (IntIntPair pair : SPECIAL_POSITIONS) {
            int x = pair.leftInt();
            int z = pair.rightInt();
            image.setRGB(x + r / 2, z + r / 2, 0x92FF33);
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i * j != 0) {
                        image.setRGB(x + r / 2 + j, z + r / 2 + i, 0x88D941);
                    }
                }
            }
        }
    }

    public static BufferedImage newImage() {
        return new BufferedImage(r, r, BufferedImage.TYPE_INT_RGB);
    }

    public static void save(String name, BufferedImage image) {
        File file = new File("C:\\Users\\69726\\Desktop\\%s.png".formatted(name));
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int adapt(int color) {
        int b = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int r = color & 0xFF;
        return (r << 16) | (g << 8) | b;
    }

    private static Pair<MapItemSavedData, Integer> createEnderSavedData(Level level, byte zoom, boolean trackingPosition, boolean unlimitedTracking) {
        MapItemSavedData data = MapItemSavedData.createFresh(0, 0, zoom, trackingPosition, unlimitedTracking, level.dimension());
        int freeMapId = level.getFreeMapId();
        level.setMapData(MapItem.makeKey(freeMapId), data);
        return Pair.of(data, freeMapId);
    }

    public static MapItemSavedData renderBiomePreviewMap(ServerLevel level, ItemStack stack) {
        MapItemSavedData data = MapItem.getSavedData(stack, level);
        data.colors = new byte[r_sqr];
        if (data != null) {
            if (level.dimension() == data.dimension) {
                int scale = 1 << data.scale;
                int centerX = data.centerX;
                int centerZ = data.centerZ;
                boolean[] watered = new boolean[r_sqr];
                int x = centerX / scale - r / 2;
                int z = centerZ / scale - r / 2;
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

                for(int dx = 0; dx < r; ++dx) {
                    for(int dz = 0; dz < r; ++dz) {
                        Holder<Biome> holder = level.getBiome(pos.set((x + dz) * scale, 0, (z + dx) * scale));
                        watered[dx * r + dz] = holder.is(BiomeTags.WATER_ON_MAP_OUTLINES);
                    }
                }

                for(int dx = 1; dx < r - 1; ++dx) {
                    for(int dz = 1; dz < r - 1; ++dz) {
                        int wateryCount = 0;

                        for(int ddx = -1; ddx < 2; ++ddx) {
                            for(int ddz = -1; ddz < 2; ++ddz) {
                                if ((ddx != 0 || ddz != 0) && isBiomeWatery(watered, dx + ddx, dz + ddz)) {
                                    ++wateryCount;
                                }
                            }
                        }

                        MapColor.Brightness brightness = MapColor.Brightness.LOWEST;
                        MapColor color = MapColor.NONE;
                        if (isBiomeWatery(watered, dx, dz)) {
                            color = MapColor.COLOR_ORANGE;
                            if (wateryCount > 7 && dz % 2 == 0) {
                                switch ((dx + (int) (Mth.sin((float) dz + 0.0F) * 7.0F)) / 8 % 5) {
                                    case 0, 4 -> brightness = MapColor.Brightness.LOW;
                                    case 1, 3 -> brightness = MapColor.Brightness.NORMAL;
                                    case 2 -> brightness = MapColor.Brightness.HIGH;
                                }
                            } else if (wateryCount > 7) {
                                color = MapColor.NONE;
                            } else if (wateryCount > 5) {
                                brightness = MapColor.Brightness.NORMAL;
                            } else if (wateryCount > 3) {
                                brightness = MapColor.Brightness.LOW;
                            } else if (wateryCount > 1) {
                                brightness = MapColor.Brightness.LOW;
                            }
                        } else if (wateryCount > 0) {
                            color = MapColor.COLOR_BROWN;
                            if (wateryCount > 3) {
                                brightness = MapColor.Brightness.NORMAL;
                            } else {
                                brightness = MapColor.Brightness.LOWEST;
                            }
                        }

                        if (color != MapColor.NONE) {
                            data.setColor(dx, dz, color.getPackedId(brightness));
                        }
                    }
                }
            }
        }
        return data;
    }


    public static void setColor(MapItemSavedData data, int x, int z, byte colorId) {
        data.colors[x + z * r] = colorId;
        ((MapItemSavedDataAccessor) data).callSetColorsDirty(x, z);
    }

    private static boolean isBiomeWatery(boolean[] watered, int x, int z) {
        return watered[z * r + x];
    }

    public static MapItemSavedData update(Level level, Entity entity, MapItemSavedData data) {
        data.colors = new byte[r_sqr];
        if (level.dimension() == data.dimension && entity instanceof Player) {
            int scale = 1 << data.scale;
            int centerX = data.centerX;
            int centerZ = data.centerZ;
            int minX = Mth.floor(entity.getX() - (double)centerX) / scale + r / 2;
            int minZ = Mth.floor(entity.getZ() - (double)centerZ) / scale + r / 2;
            int scaledR = r / scale;
            if (level.dimensionType().hasCeiling()) {
                scaledR /= 2;
            }

            MapItemSavedData.HoldingPlayer holdingPlayer = data.getHoldingPlayer((Player)entity);
            ++holdingPlayer.step;
            BlockPos.MutableBlockPos p1 = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos p2 = new BlockPos.MutableBlockPos();
            boolean flag = false;

            for(int x = minX - scaledR + 1; x < minX + scaledR; ++x) {
                if ((x & 15) == (holdingPlayer.step & 15) || flag) {
                    flag = false;
                    double d0 = 0.0D;

                    for(int z = minZ - scaledR - 1; z < minZ + scaledR; ++z) {
                        if (x >= 0 && z >= -1 && x < r && z < r) {
                            int i2 = Mth.square(x - minX) + Mth.square(z - minZ);
                            boolean flag1 = i2 > (scaledR - 2) * (scaledR - 2);
                            int j2 = (centerX / scale + x - r / 2) * scale;
                            int k2 = (centerZ / scale + z - r / 2) * scale;
                            Multiset<MapColor> multiset = LinkedHashMultiset.create();
                            LevelChunk chunk = level.getChunk(SectionPos.blockToSectionCoord(j2), SectionPos.blockToSectionCoord(k2));
                            if (!chunk.isEmpty()) {
                                int l2 = 0;
                                double d1 = 0.0D;
                                if (level.dimensionType().hasCeiling()) {
                                    int i3 = j2 + k2 * 231871;
                                    i3 = i3 * i3 * 31287121 + i3 * 11;
                                    if ((i3 >> 20 & 1) == 0) {
                                        multiset.add(Blocks.DIRT.defaultBlockState().getMapColor(level, BlockPos.ZERO), 10);
                                    } else {
                                        multiset.add(Blocks.STONE.defaultBlockState().getMapColor(level, BlockPos.ZERO), 100);
                                    }

                                    d1 = 100.0D;
                                } else {
                                    for(int i4 = 0; i4 < scale; ++i4) {
                                        for(int j3 = 0; j3 < scale; ++j3) {
                                            p1.set(j2 + i4, 0, k2 + j3);
                                            int k3 = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, p1.getX(), p1.getZ()) + 1;
                                            BlockState blockstate;
                                            if (k3 <= level.getMinBuildHeight() + 1) {
                                                blockstate = Blocks.BEDROCK.defaultBlockState();
                                            } else {
                                                do {
                                                    --k3;
                                                    p1.setY(k3);
                                                    blockstate = chunk.getBlockState(p1);
                                                } while(blockstate.getMapColor(level, p1) == MapColor.NONE && k3 > level.getMinBuildHeight());

                                                if (k3 > level.getMinBuildHeight() && !blockstate.getFluidState().isEmpty()) {
                                                    int l3 = k3 - 1;
                                                    p2.set(p1);

                                                    BlockState blockstate1;
                                                    do {
                                                        p2.setY(l3--);
                                                        blockstate1 = chunk.getBlockState(p2);
                                                        ++l2;
                                                    } while(l3 > level.getMinBuildHeight() && !blockstate1.getFluidState().isEmpty());

                                                    blockstate = getCorrectStateForFluidBlock(level, blockstate, p1);
                                                }
                                            }

                                            data.checkBanners(level, p1.getX(), p1.getZ());
                                            d1 += (double)k3 / (double)(scale * scale);
                                            multiset.add(blockstate.getMapColor(level, p1));
                                        }
                                    }
                                }

                                l2 /= scale * scale;
                                MapColor color = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MapColor.NONE);
                                MapColor.Brightness brightness;
                                if (color == MapColor.WATER) {
                                    double d2 = (double)l2 * 0.1D + (double)(x + z & 1) * 0.2D;
                                    if (d2 < 0.5D) {
                                        brightness = MapColor.Brightness.HIGH;
                                    } else if (d2 > 0.9D) {
                                        brightness = MapColor.Brightness.LOW;
                                    } else {
                                        brightness = MapColor.Brightness.NORMAL;
                                    }
                                } else {
                                    double d3 = (d1 - d0) * 4.0D / (double)(scale + 4) + ((double)(x + z & 1) - 0.5D) * 0.4D;
                                    if (d3 > 0.6D) {
                                        brightness = MapColor.Brightness.HIGH;
                                    } else if (d3 < -0.6D) {
                                        brightness = MapColor.Brightness.LOW;
                                    } else {
                                        brightness = MapColor.Brightness.NORMAL;
                                    }
                                }

                                d0 = d1;
                                if (z >= 0 && i2 < scaledR * scaledR && (!flag1 || (x + z & 1) != 0)) {
                                    flag |= updateColor(data, x, z, color.getPackedId(brightness));
                                }
                            }
                        }
                    }
                }
            }

        }
        return data;
    }

    private static BlockState getCorrectStateForFluidBlock(Level p_42901_, BlockState p_42902_, BlockPos p_42903_) {
        FluidState fluidstate = p_42902_.getFluidState();
        return !fluidstate.isEmpty() && !p_42902_.isFaceSturdy(p_42901_, p_42903_, Direction.UP) ? fluidstate.createLegacyBlock() : p_42902_;
    }

    public static boolean updateColor(MapItemSavedData data, int x, int z, byte colorId) {
        byte b0 = data.colors[x + z * r];
        if (b0 != colorId) {
            setColor(data, x, z, colorId);
            return true;
        } else {
            return false;
        }
    }
}
