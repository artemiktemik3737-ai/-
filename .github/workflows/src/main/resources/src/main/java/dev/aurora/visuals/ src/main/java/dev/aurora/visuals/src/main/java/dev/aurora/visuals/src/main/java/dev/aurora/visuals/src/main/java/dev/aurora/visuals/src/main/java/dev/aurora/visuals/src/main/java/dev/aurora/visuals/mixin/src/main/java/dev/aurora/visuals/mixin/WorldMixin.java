
package dev.aurora.visuals.mixin;

import dev.aurora.visuals.Effects;
import dev.aurora.visuals.Features;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin {
    @Inject(method = "getRainGradient", at = @At("HEAD"), cancellable = true)
    private void aurora$rain(float delta, CallbackInfoReturnable<Float> cir) {
        World self = (World) (Object) this;
        if (self.isClient() && Features.WEATHER.enabled) {
            cir.setReturnValue(Effects.rainGradient());
        }
    }

    @Inject(method = "getThunderGradient", at = @At("HEAD"), cancellable = true)
    private void aurora$thunder(float delta, CallbackInfoReturnable<Float> cir) {
        World self = (World) (Object) this;
        if (self.isClient() && Features.WEATHER.enabled) {
            cir.setReturnValue(Effects.thunderGradient());
        }
    }
}
