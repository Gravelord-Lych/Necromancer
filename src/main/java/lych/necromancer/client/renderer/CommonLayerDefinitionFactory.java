package lych.necromancer.client.renderer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public final class CommonLayerDefinitionFactory {
    private CommonLayerDefinitionFactory() {}

    public static LayerDefinition createBaseHumanoid() {
        return LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0), 64, 64);
    }
}
