/*
 * ZenCloud - Lunar-style FPS Booster
 * Inspired by Lunar Client's documented optimizations:
 *  - Particle limiting
 *  - Fast graphics / smooth lighting off
 *  - Clouds disabled
 *  - Ambient occlusion reduction
 *  - Mipmap levels reduction
 */
package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LunarFpsMod extends Mod {

    private final Minecraft mc = Minecraft.getMinecraft();

    private int     savedParticles;
    private boolean savedFancy;
    private int     savedAO;
    private int     savedMipmap;
    private boolean savedClouds;

    public LunarFpsMod() {
        super("Lunar FPS", "Lunar Client-style FPS optimisations.", Type.Performance);
        String[] profiles = {"Max Boost", "Balanced", "Quality"};
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Profile",         this, "Balanced", 0, profiles));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Limit Particles", this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Fast Graphics",   this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("No Clouds",       this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("No AO",           this, false));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Low Mipmaps",     this, true));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        savedParticles = mc.gameSettings.particleSetting;
        savedFancy     = mc.gameSettings.fancyGraphics;
        savedAO        = mc.gameSettings.ambientOcclusion;
        savedMipmap    = mc.gameSettings.mipmapLevels;
        savedClouds    = mc.gameSettings.clouds;
        applyProfile();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.gameSettings.particleSetting  = savedParticles;
        mc.gameSettings.fancyGraphics    = savedFancy;
        mc.gameSettings.ambientOcclusion = savedAO;
        mc.gameSettings.mipmapLevels     = savedMipmap;
        mc.gameSettings.clouds           = savedClouds;
        mc.renderGlobal.loadRenderers();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null) return;

        String p = Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Profile").getCurrentMode();
        if (Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Limit Particles").isCheckToggled()) {
            mc.gameSettings.particleSetting = "Max Boost".equals(p) ? 2 : "Balanced".equals(p) ? 1 : 0;
        }
        if (Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Fast Graphics").isCheckToggled()) {
            mc.gameSettings.fancyGraphics = false;
        }
        if (Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "No Clouds").isCheckToggled()) {
            mc.gameSettings.clouds = false;
        }
        if (Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "No AO").isCheckToggled()) {
            mc.gameSettings.ambientOcclusion = 0;
        }
        if (Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Low Mipmaps").isCheckToggled()) {
            mc.gameSettings.mipmapLevels = "Max Boost".equals(p) ? 0 : 2;
        }
    }

    private void applyProfile() {
        String p = Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Profile").getCurrentMode();
        switch (p) {
            case "Max Boost":
                mc.gameSettings.fancyGraphics    = false;
                mc.gameSettings.ambientOcclusion = 0;
                mc.gameSettings.particleSetting  = 2;
                mc.gameSettings.mipmapLevels     = 0;
                mc.gameSettings.clouds           = false;
                break;
            case "Balanced":
                mc.gameSettings.fancyGraphics    = false;
                mc.gameSettings.ambientOcclusion = 1;
                mc.gameSettings.particleSetting  = 1;
                mc.gameSettings.mipmapLevels     = 2;
                mc.gameSettings.clouds           = false;
                break;
            case "Quality":
                mc.gameSettings.fancyGraphics    = true;
                mc.gameSettings.ambientOcclusion = 2;
                mc.gameSettings.particleSetting  = 0;
                mc.gameSettings.mipmapLevels     = 4;
                mc.gameSettings.clouds           = true;
                break;
        }
        mc.renderGlobal.loadRenderers();
    }
}
