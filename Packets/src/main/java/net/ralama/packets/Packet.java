package net.ralama.packets;

import net.ralama.packets.api.PacketAdapter;

import java.io.Serializable;

public abstract class Packet implements Serializable {
    private static final long serialVersionUID = 123456789L;

    public final byte[] serialize() {
        return PacketAdapter.serialize(this);
    }
}