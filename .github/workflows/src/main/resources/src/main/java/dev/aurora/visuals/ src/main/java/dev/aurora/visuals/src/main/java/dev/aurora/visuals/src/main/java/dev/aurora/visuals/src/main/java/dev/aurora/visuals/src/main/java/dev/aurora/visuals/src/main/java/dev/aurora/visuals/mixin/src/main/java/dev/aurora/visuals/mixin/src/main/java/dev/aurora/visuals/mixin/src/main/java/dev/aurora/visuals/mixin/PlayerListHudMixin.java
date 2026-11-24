
package dev.aurora.visuals.mixin;

import dev.aurora.visuals.Config;
import dev.aurora.visuals.Features;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true, require = 0)
    private void aurora$tab(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        if (!Features.TAB_CUSTOMIZER.enabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        final int rgb = Config.effectColor();
        MutableText out = Text.empty();
        if (mc.player != null && entry.getProfile().getId().equals(mc.player.getUuid())) {
            out.append(Text.literal("★ ").styled(s -> s.withColor(rgb)));
        }
        out.append(cir.getReturnValue());
        int ms = entry.getLatency();
        if (ms > 0) {
            final int col = ms < 80 ? 0x55FF55 : ms < 150 ? 0xFFFF55 : 0xFF5555;
            out.append(Text.literal(" " + ms + " мс").styled(s -> s.withColor(col)));
        }
        cir.setReturnValue(out);
    }
}
