package net.ralama.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

@AllArgsConstructor
public class PlayerAuthPacket extends Packet {
    @Getter
    private final String playerName;
    @Getter
    private final boolean loggedIn;
}
