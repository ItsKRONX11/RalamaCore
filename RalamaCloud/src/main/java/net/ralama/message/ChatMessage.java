package net.ralama.message;

public record ChatMessage(String getSender, String getMessage, String getServer, long getSent) {
}
