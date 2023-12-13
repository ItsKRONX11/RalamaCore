package net.ralama.message;

import net.ralama.packets.api.Language;

public class MessageGroup {
    private final StringBuilder english = new StringBuilder();
    private final StringBuilder romanian = new StringBuilder();
    private int count = 0;

    public MessageGroup insert(String str) {
        return insert(str, str);
    }

    public MessageGroup insert(String english, String romanian) {
        this.english.insert(0,english);
        this.romanian.insert(0,romanian);
        return this;
    }

    public MessageGroup add(String str) {
        ++count;
        this.english.append("\n").append(str);
        this.romanian.append("\n").append(str);
        return this;
    }
    public MessageGroup add0(String str) {
        ++count;
        this.english.append(str);
        this.romanian.append(str);
        return this;
    }
    public MessageGroup add(String english, String romanian) {
        this.english.append(english);
        this.romanian.append(romanian);
        return this;
    }

    public int size() {
        return count;
    }

    public String toString() {
        return toString(Language.ENGLISH);
    }
    public String toString(Language language) {
        if (language == Language.ROMANIAN) {
            return romanian.toString();
        } else {
            return english.toString();
        }
    }
}
