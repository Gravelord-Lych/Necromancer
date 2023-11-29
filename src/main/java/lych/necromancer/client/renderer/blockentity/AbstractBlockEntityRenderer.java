package lych.necromancer.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    protected final BlockEntityRendererProvider.Context ctx;

    protected AbstractBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.ctx = ctx;
    }

    protected static void translateToCenter(PoseStack stack) {
        stack.pushPose();
        stack.translate(0.5F, 0.0F, 0.5F);
    }

    @Override
    public abstract void render(T carrier, float partialTicks, PoseStack stack, MultiBufferSource source, int lightColor, int overlayTexture);
}
