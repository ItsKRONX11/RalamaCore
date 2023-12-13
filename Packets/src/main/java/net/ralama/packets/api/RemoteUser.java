package net.ralama.packets.api;

import lombok.Getter;
import lombok.Setter;

public class RemoteUser {
    @Getter
    private final String name;
    @Getter
    @Setter
    private boolean loggedIn = false;
    @Getter
    @Setter
    private Language language = Language.ENGLISH;
    @Getter
    @Setter
    private int coins = 0;
    @Getter
    @Setter
    private Rank rank = Rank.PLAYER;
    @Getter
    @Setter
    private long playtime = 0L;
    @Getter
    @Setter
    private String[] muteData = null;

    public RemoteUser(String name) {
        this.name = name;
    }
}
