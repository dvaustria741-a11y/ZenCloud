package dev.cloudmc.feature.mod.impl;

import dev.cloudmc.Cloud;
import dev.cloudmc.feature.mod.Mod;
import dev.cloudmc.feature.mod.Type;
import dev.cloudmc.feature.setting.Setting;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

public class AutoScaffoldMod extends Mod {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Random rng = new Random();
    private long lastPlace = 0L;
    private int savedSlot = -1;
    private static final EnumFacing[] H = {EnumFacing.NORTH,EnumFacing.SOUTH,EnumFacing.EAST,EnumFacing.WEST};

    public AutoScaffoldMod() {
        super("Auto Scaffold", "Places blocks under your feet automatically.", Type.Movement);
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Delay", this, 500f, 80f));
        Cloud.INSTANCE.settingManager.addSetting(new Setting("Sneak", this, true));
    }

    @Override public void onDisable() {
        super.onDisable();
        if (savedSlot >= 0 && mc.thePlayer != null) { mc.thePlayer.inventory.currentItem = savedSlot; savedSlot = -1; }
        if (mc.thePlayer != null) mc.thePlayer.setSneaking(false);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;

        float delay = Cloud.INSTANCE.settingManager.getSetting("Delay", this).getCurrentNumber();
        boolean sneak = Cloud.INSTANCE.settingManager.getSetting("Sneak", this).isCheckToggled();
        long now = System.currentTimeMillis();
        long actual = (long)delay + (delay>0?(long)((rng.nextDouble()*2-1)*(delay/4)):0);
        if (now - lastPlace < actual) return;

        BlockPos place = new BlockPos(mc.thePlayer).down();
        if (solid(place)) return;

        BlockPos support = null; EnumFacing face = EnumFacing.UP;
        if (solid(place.down())) { support = place.down(); }
        else { for(EnumFacing f:H){BlockPos n=place.offset(f);if(solid(n)){support=n;face=f.getOpposite();break;}} }
        if (support == null) return;

        int bs = blockSlot(); if(bs<0)return;
        int orig = mc.thePlayer.inventory.currentItem;
        if(orig!=bs){savedSlot=orig;mc.thePlayer.inventory.currentItem=bs;}
        if(sneak)mc.thePlayer.setSneaking(true);

        float oy=mc.thePlayer.rotationYaw,op=mc.thePlayer.rotationPitch;
        mc.thePlayer.rotationYaw=(float)Math.toDegrees(Math.atan2(support.getZ()+.5-mc.thePlayer.posZ,support.getX()+.5-mc.thePlayer.posX))-90f;
        mc.thePlayer.rotationPitch=75f+rng.nextFloat()*15f;

        ItemStack stk=mc.thePlayer.inventory.getStackInSlot(bs);
        mc.playerController.onPlayerRightClick(mc.thePlayer,mc.theWorld,stk,support,face,
                new Vec3(place.getX()+.5,place.getY(),place.getZ()+.5));
        mc.thePlayer.swingItem();
        mc.thePlayer.rotationYaw=oy;mc.thePlayer.rotationPitch=op;

        if(savedSlot>=0){mc.thePlayer.inventory.currentItem=savedSlot;savedSlot=-1;}
        if(sneak)mc.thePlayer.setSneaking(false);
        lastPlace=now;
    }

    private boolean solid(BlockPos p){Material m=mc.theWorld.getBlockState(p).getBlock().getMaterial();return m!=Material.air&&m!=Material.water&&m!=Material.lava;}
    private int blockSlot(){for(int i=0;i<9;i++){ItemStack s=mc.thePlayer.inventory.getStackInSlot(i);if(s!=null&&s.getItem()instanceof ItemBlock&&s.stackSize>0)return i;}return -1;}
}
