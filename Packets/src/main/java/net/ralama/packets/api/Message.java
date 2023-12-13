package net.ralama.packets.api;

public class Message {
    private String romanian;
    private String english;

    public Message(String english, String romanian) {
        this.english = english;
        this.romanian = romanian;
    }

    public Message(Message original) {
        this.english = original.toString(Language.ENGLISH);
        this.romanian = original.toString(Language.ROMANIAN);
    }

    public String toString(Language language) {
        if (language == Language.ROMANIAN) {
            return romanian;
        } else {
            return english;
        }
    }

    public Message replaced(String placeholder, String replacement) {
        return new ReplacedMessage(this).replaced(placeholder, replacement);
    }

    private static class ReplacedMessage extends Message {
        public ReplacedMessage(Message message) {
            super(message);
        }

        @Override
        public Message replaced(String placeholder, String replacement) {
            super.english = super.english.replaceAll(placeholder, replacement == null ? "null" : replacement);
            super.romanian = super.romanian.replaceAll(placeholder, replacement == null ? "null" : replacement);
            return this;
        }
    }
}
