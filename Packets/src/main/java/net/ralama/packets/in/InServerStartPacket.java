package net.ralama.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

@AllArgsConstructor
public class InServerStartPacket extends Packet {
    @Getter
    private final int port;

}
