package lych.necromancer.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lych.necromancer.block.entity.NecrockItemBaseBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class NecrockItemBaseRenderer extends AbstractBlockEntityRenderer<NecrockItemBaseBlockEntity> {
    private static final float HEIGHT = 1.2f;
    private static final float SCALE = 0.8f;

    public NecrockItemBaseRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(NecrockItemBaseBlockEntity carrier, float partialTicks, PoseStack poseStack, MultiBufferSource source, int lightColor, int overlayTexture) {
        translateToCenter(poseStack);

        poseStack.translate(0, HEIGHT, 0);

        ItemStack itemInside = carrier.getItemInside();
        if (!itemInside.isEmpty()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(carrier.getSpin(partialTicks)));
            poseStack.scale(SCALE, SCALE, SCALE);
            
            ctx.getItemRenderer().renderStatic(itemInside,
                    ItemDisplayContext.FIXED,
                    lightColor,
                    overlayTexture,
                    poseStack,
                    source,
                    carrier.getLevel(),
                    (int) carrier.getBlockPos().asLong());
        }

        poseStack.popPose();
    }
}
