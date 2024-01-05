package lych.necromancer.util.client;

import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record RGBColorArray(float r, float g, float b, float a) {
    public RGBColorArray(float r, float g, float b) {
        this(r, g, b, 1);
    }

    public RGBColorArray(float r, float g, float b, float a) {
        this.r = Mth.clamp(r, 0, 1);
        this.g = Mth.clamp(g, 0, 1);
        this.b = Mth.clamp(b, 0, 1);
        this.a = Mth.clamp(a, 0, 1);
    }

    public float[] rgb() {
        return new float[]{r, g, b};
    }

    public float[] rgba() {
        return new float[]{r, g, b, a};
    }

    public HSBColorArray toHSB() {
        return ClientUtils.RGBtoHSB(r, g, b);
    }
}
