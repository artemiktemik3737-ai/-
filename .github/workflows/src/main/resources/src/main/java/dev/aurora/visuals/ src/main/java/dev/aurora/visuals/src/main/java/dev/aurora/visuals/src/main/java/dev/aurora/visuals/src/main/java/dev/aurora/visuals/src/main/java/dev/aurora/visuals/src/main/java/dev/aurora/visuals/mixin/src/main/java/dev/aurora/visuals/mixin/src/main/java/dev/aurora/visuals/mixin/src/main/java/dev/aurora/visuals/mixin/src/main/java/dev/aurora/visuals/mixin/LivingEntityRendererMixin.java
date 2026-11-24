
package dev.aurora.visuals.mixin;

import dev.aurora.visuals.Features;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true, require = 0)
    private void aurora$selfName(LivingEntity entity, double distance, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (Features.SELF_NAMETAG.enabled && entity == mc.player && !mc.options.getPerspective().isFirstPerson()) {
            cir.setReturnValue(true);
        }
    }
}
