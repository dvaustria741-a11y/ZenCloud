package dev.cloudmc.feature.mod;

public enum Type {

    All("explore.png"),
    Hud("display.png"),
    Mechanic("wheel.png"),
    Visual("camera.png"),
    Tweaks("crop.png"),
    Combat("sword.png"),
    Movement("movement.png"),
    Utility("utility.png"),
    Performance("performance.png");

    private final String icon;

    Type(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
