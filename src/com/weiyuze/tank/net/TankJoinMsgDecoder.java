package com.weiyuze.tank.net;

import com.weiyuze.tank.Dir;
import com.weiyuze.tank.Group;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.UUID;

public class TankJoinMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //TCP 拆包 粘包的问题；消息多长 够长处理；不够长return 等长度确定
        //4+4+4+1+4+16
        if (in.readableBytes() < 33) return;

        TankJoinMsg msg = new TankJoinMsg();//调的空的 初始值没有确定

        msg.x = in.readInt();
        msg.y = in.readInt();
        msg.dir = Dir.values()[in.readInt()];
        msg.moving = in.readBoolean();
        msg.group = Group.values()[in.readInt()];
        msg.id = new UUID(in.readLong(),in.readLong());

        //消息解析出来的对象 装进List
        out.add(msg);
    }
}
