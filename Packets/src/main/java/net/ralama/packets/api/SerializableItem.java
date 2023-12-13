package net.ralama.packets.api;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerializableItem implements Serializable {
    enum Type {
        PAPER,
        LAVA_BUCKET,
        WATER_BUCKET,
        MATERIAL_BOOK;
    }
    @Getter
    private final Type type;
    @Getter
    @Setter
    private String displayName;
    @Getter
    private final List<String> lore = new ArrayList<>();
    public SerializableItem(Type type) {
        this.type = type;
    }
    public void onClick(RemoteUser user) {}
}
