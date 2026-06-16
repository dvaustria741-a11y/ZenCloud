package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AntiAFKMod extends Mod {
    private final Minecraft mc = Minecraft.getMinecraft();
    private int ticks = 0, dir = 1;

    public AntiAFKMod() {
        super("Anti AFK", "Prevents AFK kick by rotating periodically.", Type.Utility);
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Interval", this, 120f, 30f));
    }

    @Override public void onDisable() { super.onDisable(); ticks = 0; }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null) return;
        float interval = Cloud.INSTANCE.settingManager.getSetting("Interval", this).getCurrentNumber();
        if (++ticks >= (int)(interval * 20)) {
            ticks = 0;
            mc.thePlayer.rotationYaw += 45 * dir;
            dir = -dir;
        }
    }
}
