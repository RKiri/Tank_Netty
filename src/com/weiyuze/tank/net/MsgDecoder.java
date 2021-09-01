package com.weiyuze.tank.net;

import com.weiyuze.tank.Dir;
import com.weiyuze.tank.Group;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.UUID;

public class MsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //TCP 拆包 粘包的问题；消息多长 够长处理；不够长return 等长度确定
        //4+4+4+1+4+16
        //if (in.readableBytes() < 33) return;
        if (in.readableBytes() < 8) return;

        MsgType msgType = MsgType.values()[in.readInt()];
        int length = in.readInt();

        if (in.readableBytes() < length) {
            in.resetReaderIndex();//in回到最早读指针标记位置 0位置 重置读指针
            return;//return后下一次执行从decode执行
        }

        byte[] bytes = new byte[length];//内存中new字节数组 将字节数组扔给buf
        in.readBytes(bytes);//把字节数组读满 相当于复制过来

        Msg msg = null;

        //reflection 反射
        //Class.forName(msgType.toString() + "Msg").constructor().newInstance(); 具体包名
        switch (msgType) {
            case TankJoin:
                msg = new TankJoinMsg();//调的空的 初始值没有确定
                break;
            case TankStartMoving:
                msg = new TankStartMovingMsg();
                break;
            case TankStop:
                msg = new TankStopMsg();
                break;
            default:
                break;
        }

        msg.parse(bytes);
        out.add(msg);//消息解析出来的对象 装进List
    }
}
