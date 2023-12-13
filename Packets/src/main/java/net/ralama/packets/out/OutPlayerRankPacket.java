package net.ralama.packets.out;

import lombok.Getter;
import net.ralama.packets.Packet;

public class OutPlayerRankPacket extends Packet {
    @Getter
    String name;
    @Getter
    String rank;

    public OutPlayerRankPacket(String name, String rank) {
        this.name = name;
        this.rank = rank.toUpperCase();
    }
}
