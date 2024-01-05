package lych.necromancer.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lych.necromancer.entity.monster.NecroGolem;
import lych.necromancer.util.client.ClientUtils;
import lych.necromancer.util.client.RGBColorArray;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class NecroGolemModel extends HumanoidModel<NecroGolem> {
    protected static final float MIN_SATURATION = 0.2F;
    protected static final float MAX_SATURATION = 0.8F;
    protected static final float MIN_BRIGHTNESS = 0.7F;
    protected static final float MAX_BRIGHTNESS = 1F;
    @Nullable
    protected NecroGolem golem;
    protected float r;
    protected float g;
    protected float b;
    protected float a;
    protected float ro;
    protected float go;
    protected float bo;
    protected float ao;
    private float partialTicks;

    public NecroGolemModel(ModelPart part) {
        super(part, RenderType::entityTranslucent);
        setDefaultColor();
    }

    protected void setDefaultColor() {
        this.r = NecroGolem.DEFAULT_R;
        this.g = NecroGolem.DEFAULT_G;
        this.b = NecroGolem.DEFAULT_B;
        this.a = NecroGolem.DEFAULT_A;
    }

    @Override
    public void prepareMobModel(NecroGolem golem, float limbSwing, float limbSwingAmount, float partialTicks) {
        this.partialTicks = partialTicks;
        super.prepareMobModel(golem, limbSwing, limbSwingAmount, partialTicks);
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int packedLight, int textureOverlay, float r, float g, float b, float a) {
        updateOldColor();
        updateColorFromGolem();

        if (golem != null) {
            RGBColorArray rgb = adjustColor(this.r, this.g, this.b, golem.getHealth(), golem.getMaxHealth());
            this.r = rgb.r();
            this.g = rgb.g();
            this.b = rgb.b();
        }
        r = getR(partialTicks);
        g = getG(partialTicks);
        b = getB(partialTicks);
        a = getA(partialTicks);
        super.renderToBuffer(stack, consumer, packedLight, textureOverlay, r, g, b, a);
    }

    private void updateOldColor() {
        this.ro = this.r;
        this.go = this.g;
        this.bo = this.b;
        this.ao = this.a;
    }

    private void updateColorFromGolem() {
        if (golem != null) {
            golem.getProperty().ifPresentOrElse(property -> {
                this.r = property.getR();
                this.g = property.getG();
                this.b = property.getB();
                this.a = property.getA();
            }, this::setDefaultColor);
            if (golem.getSpawnInvulTicks() > 0) {
                a = golem.getSpawnPercent();
            }
        }
    }

    @Override
    public void setupAnim(NecroGolem golem, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.golem = golem;
        super.setupAnim(golem, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    protected RGBColorArray adjustColor(float r, float g, float b, float health, float maxHealth) {
        return ClientUtils.RGBtoHSB(r, g, b)
                .lerpS(health / maxHealth, MIN_SATURATION, MAX_SATURATION)
                .lerpB(health / maxHealth, MIN_BRIGHTNESS, MAX_BRIGHTNESS)
                .toRGB();
    }

    protected float getR(float partialTicks) {
        return Mth.lerp(partialTicks, ro, r);
    }

    protected float getG(float partialTicks) {
        return Mth.lerp(partialTicks, go, g);
    }

    protected float getB(float partialTicks) {
        return Mth.lerp(partialTicks, bo, b);
    }

    protected float getA(float partialTicks) {
        return Mth.lerp(partialTicks, ao, a);
    }

    protected float getPartialTicks() {
        return partialTicks;
    }
}
