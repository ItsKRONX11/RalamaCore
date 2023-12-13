package net.ralama.ralamaproxy.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.ralama.packets.Packet;
import net.ralama.packets.api.PacketAdapter;
import net.ralama.packets.in.InProxyStartPacket;
import net.ralama.ralamaproxy.RalamaProxy;

public class ProxyHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        RalamaProxy.getInstance().setChannel(ctx.channel());

        InProxyStartPacket packet = new InProxyStartPacket(RalamaProxy.getInstance().getPort(), RalamaProxy.getInstance().getHost(), RalamaProxy.getInstance().getName());
        ctx.writeAndFlush(packet.serialize());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) {
        PacketReceiver.receivePacket((Packet) PacketAdapter.deserialize((byte[]) o), ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        e.printStackTrace();
        ctx.close();
    }
}
