package com.weiyuze.tank.net;

import com.weiyuze.tank.Tank;
import com.weiyuze.tank.TankFrame;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

public class Client {
    //Client类保存并初始化Channel 1
    private Channel channel = null;

    public static void main(String[] args) throws InterruptedException {
        Client c = new Client();
        c.connect();
    }

    //改造Client 暴露调用接口
    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();

        try {
            ChannelFuture f = b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer())
                    .connect("localhost", 8888);

            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        System.out.println("not connected!");
                    } else {
                        System.out.println("connected!");
                        //Client类保存并初始化Channel 2
                        channel = future.channel();
                    }
                }
            });
            f.sync();
            //System.out.println("...");
            f.channel().closeFuture().sync();
            System.out.println("已经退出");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    //封装Client.send(String msg)函数
    void send(String msg) {
        ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
        channel.writeAndFlush(buf);
    }

    public void closeConnect() {
        //通知服务器要退出
        send("_bye_");
    }
}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new TankJoinMsgEncoder())//多个时 有先后顺序
                .addLast(new TankJoinMsgDecoder())
                .addLast(new ClientHandler());
    }

}

//只有一种消息 用泛型指定
class ClientHandler extends SimpleChannelInboundHandler<TankJoinMsg> {
    //messageReceived Netty5被废掉
    //1.判断是不是自己 如果是 不处理
    //2.坦克列表判断是否已经有 有的话不处理
    //3.接收到任何msg 发自己的一个TankJoinMsg
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TankJoinMsg msg) throws Exception {
        if (msg.id.equals(TankFrame.INSTANCE.getMainTank().getId()) ||
                TankFrame.INSTANCE.findByUUID(msg.id) != null) return;
        System.out.println(msg);
        Tank t = new Tank(msg);
        TankFrame.INSTANCE.addTank(t);

        //send a new TankJoinMsg to the new joined tank
        ctx.writeAndFlush(new TankJoinMsg(TankFrame.INSTANCE.getMainTank()));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
        //ctx.writeAndFlush(buf);

        ctx.writeAndFlush(new TankJoinMsg(TankFrame.INSTANCE.getMainTank()));
    }

}