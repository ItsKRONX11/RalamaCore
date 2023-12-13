package net.ralama.packets.out;

import lombok.Getter;
import net.ralama.packets.Packet;

public class OutKillRequestPacket extends Packet {
    @Getter
    private final boolean global;

    public OutKillRequestPacket() {
        this(false);
    }

    public OutKillRequestPacket(boolean global) {
        this.global = global;
    }
}
