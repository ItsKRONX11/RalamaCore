package net.ralama.packets.api;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerializableInventory implements Serializable {
    @Getter
    private final int size;
    @Getter
    private final String title;
    private final Map<Integer,SerializableItem> items;
    public SerializableInventory(String title, int size) {
        this.size = size;
        this.title = title;
        this.items = new HashMap<>(size);
    }
    public SerializableItem get(int i) {
        if (i > size) return null;

        return this.items.get(i);
    }
    public void add(SerializableItem item) {
        if (items.size() >= size) return;

        this.items.put(items.size() + 1,item);
    }
    public void set(int i, SerializableItem item) {
        if (i > size) return;

        this.items.put(i, item);
    }
}
