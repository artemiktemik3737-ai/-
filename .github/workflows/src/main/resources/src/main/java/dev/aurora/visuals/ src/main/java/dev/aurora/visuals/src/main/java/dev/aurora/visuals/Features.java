
package dev.aurora.visuals;

import java.util.List;

public final class Features {
    public static final Feature SEE_INVISIBLE = new Feature("see_invisible", "See Invisible");
    public static final Feature WEATHER = new Feature("weather", "Погода");
    public static final Feature HITBOXES = new Feature("hitboxes", "Хитбоксы");
    public static final Feature JUMP_CIRCLES = new Feature("jump_circles", "Jump Circles");
    public static final Feature TRAILS = new Feature("trails", "Trails");
    public static final Feature HIT_BUBBLE = new Feature("hit_bubble", "Hit Bubble");
    public static final Feature KILL_EFFECT = new Feature("kill_effect", "Kill Effect");
    public static final Feature NIMB = new Feature("nimb", "Nimb");
    public static final Feature TARGET_ESP = new Feature("target_esp", "Target ESP");
    public static final Feature WORLD_PARTICLES = new Feature("world_particles", "World Particles");
    public static final Feature TOTEM_EFFECT = new Feature("totem_effect", "Totem Effect");
    public static final Feature SELF_NAMETAG = new Feature("self_nametag", "Self Nametag");
    public static final Feature TAB_CUSTOMIZER = new Feature("tab_customizer", "Tab Customizer");
    public static final Feature RENDER_TWEAKS = new Feature("render_tweaks", "Render Tweaks");
    public static final Feature WORLD_CUSTOMIZER = new Feature("world_customizer", "World Customizer");

    public static final List<Feature> ALL = List.of(
            SEE_INVISIBLE, WEATHER, HITBOXES, JUMP_CIRCLES, TRAILS,
            HIT_BUBBLE, KILL_EFFECT, NIMB, TARGET_ESP, WORLD_PARTICLES,
            TOTEM_EFFECT, SELF_NAMETAG, TAB_CUSTOMIZER, RENDER_TWEAKS, WORLD_CUSTOMIZER);

    private Features() {}
}
