package net.ralama.packets.api;

import lombok.NonNull;
import net.ralama.packets.Packet;

import java.io.*;

public final class PacketAdapter {
    private PacketAdapter() {
    }
    public static byte[] serialize(@NonNull Packet obj) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.flush();
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Serializable deserialize(byte[] data) {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(is);
            return (Serializable) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
