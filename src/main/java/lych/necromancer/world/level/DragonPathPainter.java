package lych.necromancer.world.level;

import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static lych.necromancer.world.level.EnderHighlighter.r;

public class DragonPathPainter {
    private static final Color START_NODE_COLOR = Color.WHITE;
    private static final Color END_NODE_COLOR = Color.GRAY;
    private static final Color EXPECTED_NODE_COLOR = Color.PINK;
    private static int id = 0;

    public static void paint(Path path, @Nullable Node startNode, Node endNode, @Nullable Node expectedToPass) {
        BufferedImage image = EnderHighlighter.newImage();
        EnderHighlighter.markSpecialPositions(image);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(START_NODE_COLOR);
        markNode(graphics, startNode, 4);
        graphics.setColor(END_NODE_COLOR);
        markNode(graphics, endNode, 4);
        graphics.setColor(EXPECTED_NODE_COLOR);
        markNode(graphics, expectedToPass, 4);
        for (int i = 0; i < path.getNodeCount(); i++) {
            Node node = path.getNode(i);
            graphics.setColor(getNodeColor(i * 2));
            markNode(graphics, node);
            if (i < path.getNodeCount() - 1) {
                graphics.setColor(getColor(i * 2 + 1));
                int x1 = offset(path.getNode(i).x);
                int y1 = offset(path.getNode(i).z);
                int x2 = offset(path.getNode(i + 1).x);
                int y2 = offset(path.getNode(i + 1).z);
                graphics.drawLine(x1, y1, x2, y2);
            }
        }
        EnderHighlighter.save("paths\\path%d".formatted(id++), image);
    }

    private static void markNode(Graphics2D graphics, @Nullable Node node) {
        markNode(graphics, node, 2);
    }

    private static void markNode(Graphics2D graphics, @Nullable Node node, int length) {
        if (node == null) {
            return;
        }
        int xo = offset(node.x - length / 2);
        int yo = offset(node.z - length / 2);
        graphics.drawRect(xo, yo, Math.min(length, r - xo + 1), Math.min(length, r - yo + 1));
    }

    private static Color getColor(int i) {
        return Color.getHSBColor(i / 40f, 0.7f, 1);
    }

    private static Color getNodeColor(int i) {
        return Color.getHSBColor(i / 40f, 0.9f, 0.5f);
    }

    private static int offset(int i) {
        return Mth.clamp(i + r / 2, 0, r);
    }
/*
    protected void tickDeath() {
        if (dragonFight != null) {
            dragonFight.updateDragon(this);
        }

        ++dragonDeathTime;
        if (dragonDeathTime >= 180 && dragonDeathTime <= 200) {
            float xOffset = (random.nextFloat() - 0.5F) * 8.0F;
            float yOffset = (random.nextFloat() - 0.5F) * 4.0F;
            float zOffset = (random.nextFloat() - 0.5F) * 8.0F;
            level().addParticle(ParticleTypes.EXPLOSION_EMITTER, getX() + (double) xOffset, getY() + 2.0D + (double) yOffset, getZ() + (double) zOffset, 0.0D, 0.0D, 0.0D);
        }

        boolean loot = level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        int xpAmount = 500;
        if (dragonFight != null && !dragonFight.hasPreviouslyKilledDragon()) {
            xpAmount = 12000;
        }

        if (level() instanceof ServerLevel) {
            if (dragonDeathTime > 150 && dragonDeathTime % 5 == 0 && loot) {
                int award = ForgeEventFactory.getExperienceDrop(this, unlimitedLastHurtByPlayer, Mth.floor((float) xpAmount * 0.08F));
                ExperienceOrb.award((ServerLevel) level(), position(), award);
            }

            if (dragonDeathTime == 1 && !isSilent()) {
                level().globalLevelEvent(1028, blockPosition(), 0);
            }
        }

        move(MoverType.SELF, new Vec3(0.0D, 0.1F, 0.0D));
        if (dragonDeathTime == 200 && level() instanceof ServerLevel) {
            if (loot) {
                int award = ForgeEventFactory.getExperienceDrop(this, unlimitedLastHurtByPlayer, Mth.floor((float) xpAmount * 0.2F));
                ExperienceOrb.award((ServerLevel) level(), position(), award);
            }

            if (dragonFight != null) {
                dragonFight.setDragonKilled(this);
            }

            remove(Entity.RemovalReason.KILLED);
            gameEvent(GameEvent.ENTITY_DIE);
        }
    }
 */
}
