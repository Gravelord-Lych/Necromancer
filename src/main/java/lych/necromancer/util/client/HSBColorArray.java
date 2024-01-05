package lych.necromancer.util.client;

import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record HSBColorArray(float h, float s, float b) {
    public HSBColorArray(float h, float s, float b) {
        this.h = Mth.clamp(h, 0, 1);
        this.s = Mth.clamp(s, 0, 1);
        this.b = Mth.clamp(b, 0, 1);
    }

    public float[] hsb() {
        return new float[]{h, s, b};
    }

    public HSBColorArray withH(float h) {
        return new HSBColorArray(h, s, b);
    }

    public HSBColorArray withS(float s) {
        return new HSBColorArray(h, s, b);
    }

    public HSBColorArray withB(float b) {
        return new HSBColorArray(h, s, b);
    }

    public HSBColorArray adjustH(float dh) {
        return withH(h + dh);
    }

    public HSBColorArray adjustS(float ds) {
        return withS(s + ds);
    }

    public HSBColorArray adjustB(float db) {
        return withB(b + db);
    }

    public HSBColorArray mulS(float mul) {
        return withS(s * mul);
    }

    public HSBColorArray mulB(float mul) {
        return withB(b * mul);
    }

    public HSBColorArray lerpH(float value, float min, float max) {
        return withH(Mth.lerp(value, min, max));
    }

    public HSBColorArray lerpS(float value, float min, float max) {
        return withS(Mth.lerp(value, min, max));
    }

    public HSBColorArray lerpB(float value, float min, float max) {
        return withB(Mth.lerp(value, min, max));
    }

    public RGBColorArray toRGB() {
        return ClientUtils.HSBtoRGB(h, s, b);
    }
}
