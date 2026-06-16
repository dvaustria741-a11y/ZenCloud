package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoSoupMod extends Mod {

    private final Minecraft mc = Minecraft.getMinecraft();
    private int prevSlot = -1;

    public AutoSoupMod() {
        super("Auto Soup", "Eats mushroom stew when low on health.", Type.Combat);
        Cloud.INSTANCE.settingManager.addSetting(new Setting("HP Threshold", this, 19f, 15f));
    }

    @Override public void onDisable() { super.onDisable(); prevSlot = -1; }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;

        if (prevSlot != -1) {
            mc.thePlayer.inventory.currentItem = prevSlot;
            prevSlot = -1;
            return;
        }

        float threshold = Cloud.INSTANCE.settingManager.getSetting("HP Threshold", this).getCurrentNumber();
        if (mc.thePlayer.getHealth() > threshold) return;

        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack s = mc.thePlayer.inventory.getStackInSlot(i);
            if (s != null && s.getItem() instanceof ItemSoup) { slot = i; break; }
        }
        if (slot == -1) return;
        prevSlot = mc.thePlayer.inventory.currentItem;
        mc.thePlayer.inventory.currentItem = slot;
        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld,
                mc.thePlayer.inventory.getStackInSlot(slot));
    }
}
