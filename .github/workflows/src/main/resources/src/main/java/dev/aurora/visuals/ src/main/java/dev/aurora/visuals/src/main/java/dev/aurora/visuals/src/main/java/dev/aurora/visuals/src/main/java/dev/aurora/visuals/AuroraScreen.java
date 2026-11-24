
package dev.aurora.visuals;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class AuroraScreen extends Screen {
    private static final int W = 430, H = 262;
    private static final String[] MODES = {"Ясно", "Дождь", "Гроза", "Снег", "Осень"};
    private static final String[] SL_NAMES = {"Сила осадков", "Ветер (снег, осень)", "Плотность (снег, осень)", "Цвет эффектов", "Время суток"};
    private static final int[] SL_MAX = {100, 100, 100, 360, 24};

    private int x0, y0;
    private int drag = -1;

    public AuroraScreen() {
        super(Text.literal("Aurora Visuals"));
    }

    @Override
    protected void init() {
        x0 = (width - W) / 2;
        y0 = (height - H) / 2;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private int get(int i) {
        return switch (i) {
            case 0 -> Config.d.strength;
            case 1 -> Config.d.wind;
            case 2 -> Config.d.density;
            case 3 -> Config.d.hue;
            default -> Config.d.hour;
        };
    }

    private void set(int i, int v) {
        switch (i) {
            case 0 -> Config.d.strength = v;
            case 1 -> Config.d.wind = v;
            case 2 -> Config.d.density = v;
            case 3 -> Config.d.hue = v;
            default -> Config.d.hour = v;
        }
    }

    private String label(int i) {
        return switch (i) {
            case 3 -> SL_NAMES[i];
            case 4 -> SL_NAMES[i] + ": " + String.format("%02d:00", get(i) % 24);
            default -> SL_NAMES[i] + ": " + get(i);
        };
    }

    private int panelX() { return x0 + 270; }
    private int panelW() { return 148; }
    private int sliderY(int i) { return y0 + 112 + i * 28; }
    private int rowX(int i) { return x0 + 12 + (i % 2) * 125; }
    private int rowY(int i) { return y0 + 34 + (i / 2) * 27; }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        super.render(c, mx, my, delta);
        int accent = 0xFF000000 | Config.effectColor();

        c.fill(x0, y0, x0 + W, y0 + H, 0xEA12122A);
        c.fill(x0, y0, x0 + W, y0 + 2, accent);
        c.drawText(textRenderer, "Aurora Visuals", x0 + 12, y0 + 12, 0xFFFFFFFF, true);
        c.drawText(textRenderer, "Esc — закрыть", x0 + W - 80, y0 + 12, 0xFF8A8AA8, false);

        for (int i = 0; i < Features.ALL.size(); i++) {
            Feature f = Features.ALL.get(i);
            int rx = rowX(i), ry = rowY(i);
            boolean hover = mx >= rx && mx < rx + 118 && my >= ry && my < ry + 22;
            c.fill(rx, ry, rx + 118, ry + 22, hover ? 0xFF272748 : 0xFF1B1B38);
            c.drawText(textRenderer, f.name, rx + 6, ry + 7, f.enabled ? 0xFFFFFFFF : 0xFF8A8AA8, false);
            int tx = rx + 118 - 28;
            c.fill(tx, ry + 6, tx + 22, ry + 16, f.enabled ? accent : 0xFF3A3A55);
            int kx = f.enabled ? tx + 13 : tx + 1;
            c.fill(kx, ry + 7, kx + 8, ry + 15, 0xFFFFFFFF);
        }

        int px = panelX(), pw = panelW();
        c.fill(px - 8, y0 + 30, px + pw, y0 + H - 10, 0xFF181834);
        c.drawText(textRenderer, "Настройки", px, y0 + 38, 0xFFFFFFFF, true);
        c.drawText(textRenderer, "Погода", px, y0 + 56, 0xFF8A8AA8, false);
        c.fill(px, y0 + 68, px + pw - 8, y0 + 88, 0xFF22223F);
        c.drawText(textRenderer, MODES[Config.d.weatherMode] + "  ›", px + 6, y0 + 74, accent, false);

        for (int i = 0; i < SL_NAMES.length; i++) {
            int sy = sliderY(i);
            c.drawText(textRenderer, label(i), px, sy - 12, 0xFFCFCFE8, false);
            int tw = pw - 8;
            if (i == 3) {
                int seg = 46;
                for (int k = 0; k < seg; k++) {
                    int col = 0xFF000000 | MathHelper.hsvToRgb(k / (float) seg, 0.65f, 1f);
                    c.fill(px + k * tw / seg, sy, px + (k + 1) * tw / seg, sy + 4, col);
                }
            } else {
                c.fill(px, sy, px + tw, sy + 4, 0xFF3A3A55);
                c.fill(px, sy, px + tw * get(i) / SL_MAX[i], sy + 4, accent);
            }
            int kx = px + tw * get(i) / SL_MAX[i] - 3;
            c.fill(kx, sy - 3, kx + 7, sy + 7, 0xFFFFFFFF);
        }
    }

    private void applySlider(int i, double mx) {
        int tw = panelW() - 8;
        double t = MathHelper.clamp((mx - panelX()) / tw, 0, 1);
        set(i, (int) Math.round(t * SL_MAX[i]));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            for (int i = 0; i < Features.ALL.size(); i++) {
                int rx = rowX(i), ry = rowY(i);
                if (mx >= rx && mx < rx + 118 && my >= ry && my < ry + 22) {
                    Features.ALL.get(i).toggle();
                    return true;
                }
            }
            int px = panelX();
            if (mx >= px && mx < px + panelW() - 8 && my >= y0 + 68 && my < y0 + 88) {
                Config.d.weatherMode = (Config.d.weatherMode + 1) % MODES.length;
                Config.save();
                return true;
            }
            for (int i = 0; i < SL_NAMES.length; i++) {
                int sy = sliderY(i);
                if (mx >= px - 4 && mx < px + panelW() && my >= sy - 6 && my < sy + 10) {
                    drag = i;
                    applySlider(i, mx);
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (drag >= 0) {
            applySlider(drag, mx);
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (drag >= 0) {
            drag = -1;
            Config.save();
        }
        return super.mouseReleased(mx, my, button);
    }
}
