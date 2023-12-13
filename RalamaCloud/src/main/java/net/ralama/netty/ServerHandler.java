package net.ralama.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.ralama.packets.Packet;
import net.ralama.packets.api.PacketAdapter;
import net.ralama.packets.base.GetterPacket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) {
        executorService.execute(() -> {
            try {
                Packet rawPacket = (Packet) PacketAdapter.deserialize((byte[]) o);

                if (rawPacket instanceof GetterPacket) {
                    Receiver.receive((GetterPacket) rawPacket, ctx);
                    return;
                }

                Receiver.receive(rawPacket, ctx);
            } catch (Exception unhandled) {
                throw new UnhandledPacketException(unhandled);
            }
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.getLocalizedMessage());
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
