package net.ralama.netty;

public class UnhandledPacketException extends RuntimeException {
    public UnhandledPacketException(Throwable cause) {
        super(cause);
    }
}
