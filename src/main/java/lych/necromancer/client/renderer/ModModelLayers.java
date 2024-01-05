package lych.necromancer.client.renderer;

import lych.necromancer.Necromancer;
import lych.necromancer.entity.ModEntityNames;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ModModelLayers {
    private static final String MAIN = "main";
    public static final ModelLayerLocation NECRO_GOLEM = create(ModEntityNames.NECRO_GOLEM);

    private ModModelLayers() {}

    private static ModelLayerLocation create(String name) {
        return create(name, MAIN);
    }

    private static ModelLayerLocation create(String model, String layer) {
        return new ModelLayerLocation(Necromancer.prefix(model), layer);
    }
}
