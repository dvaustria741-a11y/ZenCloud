/*
 * ZenCloud - KillAura module
 */
package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class KillAuraMod extends Mod {

    private final Minecraft mc = Minecraft.getMinecraft();
    private EntityLivingBase lockedTarget = null;
    private long lastAttack = 0L;

    public KillAuraMod() {
        super("Kill Aura", "Automatically attacks nearby entities.", Type.Combat);
        // range: max=6, current=3.5
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Range",    this, 6f,  3.5f));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("CPS",      this, 20f, 12f));
        String[] targets = {"Players", "Mobs", "All"};
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Target",   this, "Players", 0, targets));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Focus",    this, true));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Rotations",this, true));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;

        float range = Cloud.INSTANCE.settingManager.getSetting("Range",  this).getCurrentNumber();
        float cps   = Cloud.INSTANCE.settingManager.getSetting("CPS",    this).getCurrentNumber();
        String targetMode = Cloud.INSTANCE.settingManager.getSetting("Target", this).getCurrentMode();
        boolean focus     = Cloud.INSTANCE.settingManager.getSetting("Focus",     this).isCheckToggled();
        boolean rotations = Cloud.INSTANCE.settingManager.getSetting("Rotations", this).isCheckToggled();

        long delay = (long)(1000.0 / cps);
        long now = System.currentTimeMillis();
        if (now - lastAttack < delay) return;

        if (focus && lockedTarget != null && isValid(lockedTarget, range, targetMode)) {
            // keep locked
        } else {
            lockedTarget = findNearest(range, targetMode);
        }
        if (lockedTarget == null) return;

        if (rotations) rotate(lockedTarget);
        lastAttack = now;
        mc.thePlayer.swingItem();
        mc.playerController.attackEntity(mc.thePlayer, lockedTarget);
        if (mc.thePlayer.isSprinting()) mc.thePlayer.setSprinting(true);
    }

    @Override public void onDisable() { super.onDisable(); lockedTarget = null; }

    private boolean isValid(EntityLivingBase e, float range, String mode) {
        if (e == mc.thePlayer || e.getHealth() <= 0) return false;
        if (mc.thePlayer.getDistanceToEntity(e) > range) return false;
        if ("Players".equals(mode) && !(e instanceof EntityPlayer)) return false;
        if ("Mobs".equals(mode)    &&  (e instanceof EntityPlayer)) return false;
        return true;
    }

    private EntityLivingBase findNearest(float range, String mode) {
        EntityLivingBase best = null; double bestD = Double.MAX_VALUE;
        for (Object o : mc.theWorld.loadedEntityList) {
            if (!(o instanceof EntityLivingBase)) continue;
            EntityLivingBase e = (EntityLivingBase) o;
            if (!isValid(e, range, mode)) continue;
            double d = mc.thePlayer.getDistanceToEntity(e);
            if (d < bestD) { bestD = d; best = e; }
        }
        return best;
    }

    private void rotate(EntityLivingBase e) {
        double dx = e.posX - mc.thePlayer.posX;
        double dy = (e.posY + e.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dz = e.posZ - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(dx*dx + dz*dz);
        mc.thePlayer.rotationYaw   = (float)Math.toDegrees(Math.atan2(dz,dx)) - 90f;
        mc.thePlayer.rotationPitch = (float)-Math.toDegrees(Math.atan2(dy,dist));
    }
}
