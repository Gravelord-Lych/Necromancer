package lych.necromancer.client.renderer;

import lych.necromancer.Necromancer;
import lych.necromancer.client.model.NecroGolemModel;
import lych.necromancer.entity.monster.NecroGolem;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class NecroGolemRenderer extends HumanoidNecromancyGeneratedMobRenderer<NecroGolem, NecroGolemModel> {
    private static final ResourceLocation NECRO_GOLEM = Necromancer.prefixTex("entity/necromancy_created/necro_golem.png");

    public NecroGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new NecroGolemModel(context.bakeLayer(ModModelLayers.NECRO_GOLEM)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(NecroGolem golem) {
        return NECRO_GOLEM;
    }
}
