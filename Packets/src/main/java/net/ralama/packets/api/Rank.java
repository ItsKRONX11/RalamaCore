package net.ralama.packets.api;

import lombok.Getter;

public enum Rank {
    OWNER(160, "Owner", "§0§lOwner", '0', "STAFF", 1097184726491598889L, 1114220603822575626L),
    COOWNER(160, "CoOwner", "§0§lCoOwner", '0', "STAFF", 1114220603822575626L),
    SRDEVELOPER(155, "SrDeveloper", "§c§lSrDeveloper", 'c', "SrDEV", 1134831763907149895L, 1134832000973414460L, 1114220603822575626L),
    MANAGER(150, "Manager", "§4§lManager", '4', "STAFF", 1114220287966314506L, 1114220603822575626L),
    COORDINATOR(145, "Coordinator", "§3§lCoordinator", '3', "§3STAFF"),
    DEVELOPER(140, "Developer", "§c§lDeveloper", 'c', "DEV", 1134832000973414460L),
    MODERATOR(120, "Moderator", "§5§lModerator", '5', "STAFF", 1143663612930572510L, 1114220603822575626L),
    HELPER(100, "Helper", "§9§lHelper", '9', "STAFF", 1143663443073843262L, 1114220603822575626L),
    BUILDER(90, "Builder", "§3§lBuilder", '3', "BUILD", 1143663321057341461L),
    YOUTUBE(80, "YouTube", "§f§lYou§c§lTube", 'c', "YT", 1143663142015074404L),
    TIKTOK(80, "TikTok", "§b§lT§c§li§b§lk§c§lT§b§lo§c§lk", 'c', "TT", 1143663142015074404L),
    OG(75, "OG", "§e§lOG", 'e', "§eOG", 1143662958497517679L),
    EMERALD(70, "Emerald", "§2Emerald", '2', "Emerald", 1143662815543050341L),
    DIAMOND(60, "Diamond", "§bDiamond", 'b', "Diamond", 1143662652132954122L),
    IRON(50, "Iron", "§6Iron", '6', "Iron", 1143662345143459860L),
    PLAYER(10, "Player", "Player", 'f', "", 1143874838629458000L);

    @Getter
    private final int accessLevel;
    @Getter
    private final long[] discordId;
    @Getter
    private final String name;
    @Getter
    private final String displayName;
    @Getter
    private final String color;
    @Getter
    private final String shortName;

    Rank(int accessLevel, String name, String displayName, char color, String shortName, long... discordId) {
        this.discordId = discordId;
        this.accessLevel = accessLevel;
        this.name = name;
        this.displayName = displayName;
        this.color = "§" + color;
        this.shortName = shortName;
    }

    public boolean equalsIsHigher(Rank rank) {
        return this.getAccessLevel() >= rank.getAccessLevel();
    }

    public boolean equalsIsLower(Rank rank) {
        return this.getAccessLevel() <= rank.getAccessLevel();
    }

    public boolean isHigher(Rank rank) {
        return this.getAccessLevel() > rank.getAccessLevel();
    }

    public boolean equals(Rank rank) {
        return this.getAccessLevel() == rank.getAccessLevel();
    }

    public boolean isStaff() {
        return this.equalsIsHigher(Rank.HELPER);
    }

}

