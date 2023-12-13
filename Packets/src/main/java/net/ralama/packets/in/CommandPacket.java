package net.ralama.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

@AllArgsConstructor
public class CommandPacket extends Packet {
    @Getter
    private final String command;
    @Getter
    private final String[] args;
    @Getter
    private final String name;
}
