package net.ralama.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

@AllArgsConstructor
public class PlayerLobbyLocationPacket extends Packet {
    @Getter
    private final String playerName;
    @Getter
    private final double x;
    @Getter
    private final double y;
    @Getter
    private final double z;
    @Getter
    private final float pitch;
    @Getter
    private final float yaw;
}
