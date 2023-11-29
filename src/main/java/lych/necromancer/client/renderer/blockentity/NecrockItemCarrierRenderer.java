package lych.necromancer.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lych.necromancer.block.entity.NecrockItemCarrierBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NecrockItemCarrierRenderer extends AbstractBlockEntityRenderer<NecrockItemCarrierBlockEntity> {
    public static final float SCALE = 0.6F;

    public NecrockItemCarrierRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(NecrockItemCarrierBlockEntity carrier, float partialTicks, PoseStack poseStack, MultiBufferSource source, int lightColor, int overlayTexture) {
        translateToCenter(poseStack);
        poseStack.translate(0, 0.1875F, 0);

        ItemStack itemInside = carrier.getItemInside();
        if (!itemInside.isEmpty()) {
            poseStack.translate(0, 0.03125F, 0);
            poseStack.scale(SCALE, SCALE, SCALE);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));

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
