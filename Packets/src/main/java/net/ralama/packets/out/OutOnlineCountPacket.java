package net.ralama.packets.out;

import lombok.Getter;
import net.ralama.packets.Packet;

public class OutOnlineCountPacket extends Packet {
    @Getter
    private int online;

    public OutOnlineCountPacket(int online) {
        this.online = online;
    }
}
