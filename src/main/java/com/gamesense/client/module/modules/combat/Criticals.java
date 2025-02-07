package com.gamesense.client.module.modules.combat;

import java.util.Arrays;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;


/*
 * @author hausemasterissue
 * @credits sxmurai
 * @since 9/29/2021
 */

@Module.Declaration(name = "Criticals", category = Category.Combat)
public class Criticals extends Module {
	
	ModeSetting critMode = registerMode("Mode", Arrays.asList("Packet", "Jump", "NCPStrict"), "NCPStrict");

	CPacketUseEntity packet;
	final Minecraft mc = Minecraft.getMinecraft();

    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (PacketEvent.getPacket() instanceof CPacketUseEntity) {
        	packet = (CPacketUseEntity)PacketEvent.getPacket();
            if (((CPacketUseEntity) PacketEvent.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)this.mc.world) instanceof EntityLivingBase &&
            		mc.player.onGround && !mc.player.isInLava() && !mc.player.isInWater()) {
            	switch (critMode.getValue()) {
                case "Jump": {
                    mc.player.jump();
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.05, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                    break;
                }

                case "Packet": {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.3, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                    break;
                }

                case "NCPStrict": {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.062602401692772D, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0726023996066094D, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                    break;
                }
            }
        }
            }
        }
    );
    
    public String getHudInfo() {
        String t = "";
        if (critMode.getValue().equalsIgnoreCase("Packet")){
            t = "[" + ChatFormatting.WHITE + critMode.getValue() + ChatFormatting.GRAY + "]";
        } else if (critMode.getValue().equalsIgnoreCase("Jump")) {
        	t = "[" + ChatFormatting.WHITE + critMode.getValue() + ChatFormatting.GRAY + "]";
        } else if (critMode.getValue().equalsIgnoreCase("NCPStrict")) {
        	t = "[" + ChatFormatting.WHITE + critMode.getValue() + ChatFormatting.GRAY + "]";
        }

        return t;
    }
    
	
}


