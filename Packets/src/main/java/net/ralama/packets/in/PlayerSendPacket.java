package net.ralama.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

@AllArgsConstructor
public class PlayerSendPacket extends Packet {
    @Getter
    private final String playerName;
    @Getter
    private final String serverName;
}
