package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import net.minecraft.network.Packet;

public class PacketEvent extends GameSenseEvent {

    public static Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        super();
        PacketEvent.packet = packet;
    }

    public static Packet<?> getPacket() {
        return packet;
    }

    public static class Receive extends PacketEvent {

        public Receive(Packet<?> packet) {
            super(packet);
        }
    }

    public static class Send extends PacketEvent {
        public Send(Packet<?> packet) {
            super(packet);
        }
    }

    public static class PostReceive extends PacketEvent {
        public PostReceive(Packet<?> packet) {
            super(packet);
        }
    }

    public static class PostSend extends PacketEvent {
        public PostSend(Packet<?> packet) {
            super(packet);
        }
    }
}
