package com.gamesense.client.manager.managers;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.RenderEntityEvent;
import com.gamesense.api.util.misc.CollectionUtils;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.client.manager.Manager;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.type.EventPriority;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

// Sponsored by KAMI Blue
// https://github.com/kami-blue/client/blob/master/src/main/kotlin/org/kamiblue/client/manager/managers/PlayerPacketManager.kt
public enum PlayerPacketManager implements Manager {
    INSTANCE;

    private final List<PlayerPacket> packets = new ArrayList<>();

    private Vec3d prevServerSidePosition = Vec3d.ZERO;
    private Vec3d serverSizePosition = Vec3d.ZERO;

    private Vec2f prevServerSideRotation = Vec2f.ZERO;
    private Vec2f serverSideRotation = Vec2f.ZERO;

    private Vec2f clientSidePitch = Vec2f.ZERO;

    @SuppressWarnings("unused")
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<>(event -> {
        if (event.getPhase() != Phase.BY) return;

        if (!packets.isEmpty()) {
            PlayerPacket packet = CollectionUtils.maxOrNull(packets, PlayerPacket::getPriority);

            if (packet != null) {
                event.apply(packet);
            }

            packets.clear();
        }
    });

    @SuppressWarnings("unused")
    private final Listener<PacketEvent.PostSend> postSendListener = new Listener<>(event -> {
        if (event.isCancelled()) return;

        Packet<?> rawPacket = event.getPacket();

        if (rawPacket instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) rawPacket;

            if (packet.moving) {
                serverSizePosition = new Vec3d(packet.x, packet.y, packet.z);
            }

            if (packet.rotating) {
                serverSideRotation = new Vec2f(packet.yaw, packet.pitch);

                EntityPlayerSP player = getPlayer();
                if (player != null) {
                    player.rotationYawHead = packet.yaw;
                }
            }
        }
    }, EventPriority.LOWEST);

    @SuppressWarnings("unused")
    private final Listener<TickEvent.ClientTickEvent> tickEventListener = new Listener<>(event -> {
        if (event.phase != TickEvent.Phase.START) return;

        prevServerSidePosition = serverSizePosition;
        prevServerSideRotation = serverSideRotation;
    });

    @SuppressWarnings("unused")
    private final Listener<RenderEntityEvent.Head> renderEntityEventHeadListener = new Listener<>(event -> {
        EntityPlayerSP player = getPlayer();

        if (player == null || player.isRiding() || event.getEntity() != player) return;

        clientSidePitch = new Vec2f(player.prevRotationPitch, player.rotationPitch);
        player.prevRotationPitch = prevServerSideRotation.y;
        player.rotationPitch = serverSideRotation.y;
    });

    @SuppressWarnings("unused")
    private final Listener<RenderEntityEvent.Return> renderEntityEventReturnListener = new Listener<>(event -> {
        EntityPlayerSP player = getPlayer();

        if (player == null || player.isRiding() || event.getEntity() != player) return;

        player.prevRotationPitch = clientSidePitch.x;
        player.rotationPitch = clientSidePitch.y;
    });

    public void addPacket(PlayerPacket packet) {
        packets.add(packet);
    }

    public Vec3d getPrevServerSidePosition() {
        return prevServerSidePosition;
    }

    public Vec3d getServerSizePosition() {
        return serverSizePosition;
    }

    public Vec2f getPrevServerSideRotation() {
        return prevServerSideRotation;
    }

    public Vec2f getServerSideRotation() {
        return serverSideRotation;
    }

}
