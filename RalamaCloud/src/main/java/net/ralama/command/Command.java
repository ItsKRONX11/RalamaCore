package net.ralama.command;

import lombok.Getter;
import net.ralama.packets.api.Rank;
import net.ralama.player.RalamaPlayer;

public abstract class Command {
    @Getter
    private final String name;
    @Getter
    private final String[] aliases;
    @Getter
    private final Rank rank;

    public Command(String name, Rank rank, String... aliases) {
        this.name = name;
        this.aliases = aliases;
        this.rank = rank;
    }

    public Command(String name, String... aliases) {
        this(name, Rank.PLAYER, aliases);
    }

    public Command(String name, Rank rank) {
        this(name, rank, new String[0]);
    }

    public Command(String name) {
        this(name, Rank.PLAYER);
    }

    public abstract void execute(RalamaPlayer sender, String[] args) throws Exception;
}
