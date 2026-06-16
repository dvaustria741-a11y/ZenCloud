package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FpsBoosterMod extends Mod {
    private final Minecraft mc = Minecraft.getMinecraft();
    private int savedParticles; private boolean savedFancy; private int savedAO;

    public FpsBoosterMod() {
        super("FPS Booster", "Optimises rendering for better performance.", Type.Performance);
        String[] profiles = {"Performance","Balanced","Quality"};
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Profile",         this, "Balanced", 0, profiles));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Limit Particles", this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Fast Graphics",   this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("No AO",           this, false));
    }

    @Override public void onEnable() {
        super.onEnable();
        savedParticles=mc.gameSettings.particleSetting; savedFancy=mc.gameSettings.fancyGraphics; savedAO=mc.gameSettings.ambientOcclusion;
        String p=Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Profile").getCurrentMode();
        if("Performance".equals(p)){mc.gameSettings.fancyGraphics=false;mc.gameSettings.ambientOcclusion=0;mc.gameSettings.particleSetting=2;}
        else if("Balanced".equals(p)){mc.gameSettings.fancyGraphics=false;mc.gameSettings.ambientOcclusion=1;mc.gameSettings.particleSetting=1;}
        else{mc.gameSettings.fancyGraphics=true;mc.gameSettings.ambientOcclusion=2;mc.gameSettings.particleSetting=0;}
    }

    @Override public void onDisable() {
        super.onDisable();
        mc.gameSettings.particleSetting=savedParticles; mc.gameSettings.fancyGraphics=savedFancy; mc.gameSettings.ambientOcclusion=savedAO;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if(Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Limit Particles").isCheckToggled()) mc.gameSettings.particleSetting=2;
        if(Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "Fast Graphics").isCheckToggled()) mc.gameSettings.fancyGraphics=false;
        if(Cloud.INSTANCE.settingManager.getSettingByModAndName(getName(), "No AO").isCheckToggled()) mc.gameSettings.ambientOcclusion=0;
    }
}
