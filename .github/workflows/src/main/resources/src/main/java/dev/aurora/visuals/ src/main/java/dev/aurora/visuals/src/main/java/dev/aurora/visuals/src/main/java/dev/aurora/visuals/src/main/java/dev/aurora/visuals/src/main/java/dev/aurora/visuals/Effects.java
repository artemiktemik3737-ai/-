
package dev.aurora.visuals;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;

public final class Effects {
    private static boolean wasOnGround = true;
    private static boolean hitboxesApplied = false;
    private static boolean glowSet = false;
    private static LivingEntity target;
    private static int targetTicks;
    private static boolean tweaksApplied = false;
    private static boolean savedBob = true;
    private static double savedFov = 1.0;

    public static float rainGradient() {
        int m = Config.d.weatherMode;
        return (m == 1 || m == 2) ? Config.d.strength / 100f : 0f;
    }

    public static float thunderGradient() {
        return Config.d.weatherMode == 2 ? Config.d.strength / 100f : 0f;
    }

    public static void onAttack(LivingEntity le) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld w = mc.world;
        if (w == null) return;

        if (target != null && target != le) endTarget();
        target = le;
        targetTicks = 100;

        if (Features.TARGET_ESP.enabled) {
            le.setGlowing(true);
            glowSet = true;
        }
        if (Features.HIT_BUBBLE.enabled) {
            DustParticleEffect dust = new DustParticleEffect(Config.effectColor(), 0.8f);
            double r = le.getWidth() * 0.9 + 0.35;
            sphere(w, dust, le.getX(), le.getY() + le.getHeight() / 2, le.getZ(), r, 46, 0.02);
        }
    }

    private static void endTarget() {
        if (target != null && glowSet) target.setGlowing(false);
        target = null;
        glowSet = false;
    }

    public static void tick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        ClientWorld w = mc.world;
        if (p == null || w == null) {
            endTarget();
            return;
        }
        Random r = w.random;
        int rgb = Config.effectColor();
        DustParticleEffect dust = new DustParticleEffect(rgb, 1.0f);
        DustParticleEffect small = new DustParticleEffect(rgb, 0.6f);
        boolean firstPerson = mc.options.getPerspective().isFirstPerson();

        boolean wantBoxes = Features.HITBOXES.enabled;
        if (wantBoxes != hitboxesApplied) {
            mc.getEntityRenderDispatcher().setRenderHitboxes(wantBoxes);
            hitboxesApplied = wantBoxes;
        }

        boolean wantTweaks = Features.RENDER_TWEAKS.enabled;
        if (wantTweaks && !tweaksApplied) {
            savedBob = mc.options.getBobView().getValue();
            savedFov = mc.options.getFovEffectScale().getValue();
            mc.options.getBobView().setValue(false);
            mc.options.getFovEffectScale().setValue(0.0);
            tweaksApplied = true;
        } else if (!wantTweaks && tweaksApplied) {
            mc.options.getBobView().setValue(savedBob);
            mc.options.getFovEffectScale().setValue(savedFov);
            tweaksApplied = false;
        }

        if (Features.WORLD_CUSTOMIZER.enabled) {
            w.setTimeOfDay(hourToTicks());
        }

        if (Features.JUMP_CIRCLES.enabled && wasOnGround && !p.isOnGround() && p.getVelocity().y > 0.1) {
            ring(w, dust, p.getX(), p.getY() + 0.05, p.getZ(), 0.5, 30, 0.07);
        }
        wasOnGround = p.isOnGround();

        if (Features.TRAILS.enabled || Features.NIMB.enabled) {
            for (AbstractClientPlayerEntity o : w.getPlayers()) {
                boolean self = o == p;

                if (Features.TRAILS.enabled) {
                    double dx = o.getX() - o.prevX;
                    double dz = o.getZ() - o.prevZ;
                    if (dx * dx + dz * dz > 0.0004) {
                        w.addParticle(dust,
                                o.getX() + (r.nextDouble() - 0.5) * 0.3, o.getY() + 0.1,
                                o.getZ() + (r.nextDouble() - 0.5) * 0.3, 0, 0, 0);
                    }
                }

                if (Features.NIMB.enabled && !(self && firstPerson) && w.getTime() % 2 == 0) {
                    double base = w.getTime() * 0.2;
                    for (int k = 0; k < 4; k++) {
                        double a = base + k * Math.PI / 2;
                        w.addParticle(small,
                                o.getX() + Math.cos(a) * 0.32,
                                o.getY() + o.getHeight() + 0.28,
                                o.getZ() + Math.sin(a) * 0.32, 0, 0, 0);
                    }
                }
            }
        }

        if (Features.WORLD_PARTICLES.enabled) {
            int n = 1 + Config.d.density / 25;
            for (int i = 0; i < n; i++) {
                w.addParticle(ParticleTypes.END_ROD,
                        p.getX() + (r.nextDouble() - 0.5) * 24,
                        p.getY() + r.nextDouble() * 6,
                        p.getZ() + (r.nextDouble() - 0.5) * 24,
                        (r.nextDouble() - 0.5) * 0.01, 0.005, (r.nextDouble() - 0.5) * 0.01);
            }
        }

        if (Features.WEATHER.enabled && (Config.d.weatherMode == 3 || Config.d.weatherMode == 4)) {
            int n = Config.d.density / 8;
            double drift = Config.d.wind / 100.0 * 0.2;
            for (int i = 0; i < n; i++) {
                double x = p.getX() + (r.nextDouble() - 0.5) * 28;
                double y = p.getY() + 4 + r.nextDouble() * 8;
                double z = p.getZ() + (r.nextDouble() - 0.5) * 28;
                if (Config.d.weatherMode == 3) {
                    w.addParticle(ParticleTypes.SNOWFLAKE, x, y, z,
                            drift + (r.nextDouble() - 0.5) * 0.02, -0.12, (r.nextDouble() - 0.5) * 0.02);
                } else {
                    w.addParticle(leaf(r), x, y, z, drift, -0.04, 0);
                }
            }
        }

        if (target != null) {
            targetTicks--;
            if (target.isDead() || target.getHealth() <= 0f) {
                if (Features.KILL_EFFECT.enabled) burst(w, target, dust, r);
                endTarget();
            } else if (targetTicks <= 0 || target.isRemoved()) {
                endTarget();
            }
        }
    }

    public static long hourToTicks() {
        return ((Config.d.hour + 18) % 24) * 1000L;
    }

    public static void totemBurst(Entity e) {
        if (!Features.TOTEM_EFFECT.enabled) return;
        ClientWorld w = MinecraftClient.getInstance().world;
        if (w == null) return;
        Random r = w.random;
        DustParticleEffect dust = new DustParticleEffect(Config.effectColor(), 1.2f);
        double cx = e.getX(), cy = e.getY() + e.getHeight() / 2, cz = e.getZ();
        for (int i = 0; i < 60; i++) {
            w.addParticle(dust, cx, cy, cz,
                    (r.nextDouble() - 0.5) * 0.9, r.nextDouble() * 0.8, (r.nextDouble() - 0.5) * 0.9);
        }
        sphere(w, dust, cx, cy, cz, 0.8, 50, 0.2);
    }

    private static ParticleEffect leaf(Random r) {
        var block = switch (r.nextInt(3)) {
            case 0 -> Blocks.ORANGE_WOOL;
            case 1 -> Blocks.RED_WOOL;
            default -> Blocks.YELLOW_WOOL;
        };
        return new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, block.getDefaultState());
    }

    private static void burst(ClientWorld w, LivingEntity e, DustParticleEffect dust, Random r) {
        double cx = e.getX(), cy = e.getY() + e.getHeight() / 2, cz = e.getZ();
        for (int i = 0; i < 36; i++) {
            w.addParticle(ParticleTypes.TOTEM_OF_UNDYING, cx, cy, cz,
                    (r.nextDouble() - 0.5) * 0.9, r.nextDouble() * 0.7, (r.nextDouble() - 0.5) * 0.9);
        }
        sphere(w, dust, cx, cy, cz, 0.6, 40, 0.15);
    }

    private static void ring(ClientWorld w, ParticleEffect e, double cx, double cy, double cz,
                             double radius, int n, double out) {
        for (int i = 0; i < n; i++) {
            double a = Math.PI * 2 * i / n;
            double dx = Math.cos(a), dz = Math.sin(a);
            w.addParticle(e, cx + dx * radius, cy, cz + dz * radius, dx * out, 0.01, dz * out);
        }
    }

    private static void sphere(ClientWorld w, ParticleEffect e, double cx, double cy, double cz,
                               double radius, int n, double out) {
        double golden = Math.PI * (3 - Math.sqrt(5));
        for (int i = 0; i < n; i++) {
            double y = 1 - (i / (double) (n - 1)) * 2;
            double rad = Math.sqrt(1 - y * y);
            double th = golden * i;
            double dx = Math.cos(th) * rad, dz = Math.sin(th) * rad;
            w.addParticle(e, cx + dx * radius, cy + y * radius, cz + dz * radius,
                    dx * out, y * out, dz * out);
        }
    }

    private Effects() {}
}
