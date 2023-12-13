package net.ralama.packets.in;

import lombok.Getter;
import net.ralama.packets.Packet;

public class InServerAlivePacket extends Packet {
    @Getter
    private final String name;
    @Getter
    private final int maxPlayers;
    @Getter
    private final int usedMemory;

    public InServerAlivePacket(String name, int usedMemory, int maxPlayers) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.usedMemory = usedMemory;
    }
}
