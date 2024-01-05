package lych.necromancer.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import lych.necromancer.entity.monster.NecromancyGenerated;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;

public abstract class HumanoidNecromancyGeneratedMobRenderer<T extends NecromancyGenerated, M extends HumanoidModel<T>> extends HumanoidMobRenderer<T, M> implements NecromancyCreationRenderer<T> {
    public HumanoidNecromancyGeneratedMobRenderer(EntityRendererProvider.Context context, M model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    public HumanoidNecromancyGeneratedMobRenderer(EntityRendererProvider.Context context, M model, float shadowRadius, float headScaleX, float headScaleY, float headScaleZ) {
        super(context, model, shadowRadius, headScaleX, headScaleY, headScaleZ);
    }

    @Override
    protected void scale(T mob, PoseStack stack, float partialTicks) {
        NecromancyGenerated.Size size = mob.getSize();
        if (size == NecromancyGenerated.Size.LARGE) {
            float scale = size.getScale();
            stack.scale(scale, scale, scale);
        }
    }
}
