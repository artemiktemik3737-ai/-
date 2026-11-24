package dev.aurora.visuals;

public final class Feature {
    public final String id;
    public final String name;
    public boolean enabled;

    public Feature(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void toggle() {
        enabled = !enabled;
        Config.save();
    }
}
