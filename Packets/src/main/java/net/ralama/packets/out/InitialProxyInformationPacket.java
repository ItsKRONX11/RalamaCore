package net.ralama.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.packets.Packet;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class InitialProxyInformationPacket extends Packet {
    @Getter
    private final Map<String, InetSocketAddress> servers;
    @Getter
    private final String motd;
    @Getter
    private final int online;
    @Getter
    private final List<String> filterEntries;
    @Getter
    private final List<String> commands;
}
