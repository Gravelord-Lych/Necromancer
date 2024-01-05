package lych.necromancer.util.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.Color;

@OnlyIn(Dist.CLIENT)
public final class ClientUtils {
    private ClientUtils() {}

    public static HSBColorArray RGBtoHSB(RGBColorArray array) {
        return RGBtoHSB(array.r(), array.g(), array.b());
    }

    public static HSBColorArray RGBtoHSB(float r, float g, float b) {
        float[] hsb = Color.RGBtoHSB((int) (r * 255), (int) (g * 255), (int) (b * 255), null);
        return new HSBColorArray(hsb[0], hsb[1], hsb[2]);
    }

    public static RGBColorArray HSBtoRGB(float h, float s, float b) {
        Color color = Color.getHSBColor(h, s, b);
        if (color.getRGB() < 16777216) {
            return new RGBColorArray(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1);
        }
        return new RGBColorArray(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }
}
