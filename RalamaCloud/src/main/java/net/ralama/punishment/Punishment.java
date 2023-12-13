package net.ralama.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ralama.Ralama;
import net.ralama.player.RalamaPlayer;

@AllArgsConstructor
public class Punishment {
    private final String player;
    private final String sender;
    @Getter
    private final long end;
    @Getter
    private final long created;
    @Getter
    private final String reason;
    @Getter
    private final PunishmentType type;


    public RalamaPlayer getPlayer() {
        return Ralama.getPlayer(player);
    }

    public RalamaPlayer getSender() {
        return Ralama.getPlayer(sender);
    }

    public String[] toSaveArray() {
        return new String[]{Long.toString(created), Long.toString(end), reason, sender};
    }

    public String toString() {
        return String.join("§", this.toSaveArray());
    }

    public static Punishment fromSaveArray(String[] array, PunishmentType type) {
        return new Punishment(null, array[3], Long.parseLong(array[1]), Long.parseLong(array[0]), array[2], type);
    }
}
