package net.ralama.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

@AllArgsConstructor
public class PlayerConnectPacket extends Packet {
    @Getter
    private final String server;
    @Getter
    private final String name;
}
