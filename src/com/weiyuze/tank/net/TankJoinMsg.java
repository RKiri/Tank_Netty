package com.weiyuze.tank.net;

import com.weiyuze.tank.Dir;
import com.weiyuze.tank.Group;
import com.weiyuze.tank.Tank;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class TankJoinMsg {
    public int x, y;
    public Dir dir;
    public boolean moving;
    public Group group;
    public UUID id;

    public TankJoinMsg(Tank t) {//通过坦克初始化msg
        this.x = t.getX();
        this.y = t.getY();
        this.dir = t.getDir();
        this.moving = t.isMoving();
        this.group = t.getGroup();
        this.id = t.getId();
    }

    public TankJoinMsg(int x, int y, Dir dir, boolean moving, Group group, UUID id) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.moving = moving;
        this.group = group;
        this.id = id;
    }

    public TankJoinMsg() {

    }

    //自定义协议 netty.io 官网 factorial例子 长度不固定
    //消息头：消息类型
    //4个字节 int类型数 告诉后面消息多长 处理多长
    //校验码 根据前面的内容算出验证码；中途内容修改 校验码不正确 消息是否被篡改

    //字符串协议在基础的协议封装了一层
    //HTTP协议建立在TCP之上
    //TCP协议不用字符串

    //将消息转为字节数组
    //ByteBuf 消息绑定在Netty上；当不想用Netty 需要重新写
    //用JDK自带来写 可复用
    //"123" UTF-8 3个字节 “123456” 6个字节 ；123 4个字节，123456 4个字节
    //一个字节8位
    public byte[] toBytes() {
        ByteArrayOutputStream baos = null;//往字节数组里写
        DataOutputStream dos = null;//专门用来写数据
        byte[] bytes = null;

        try {
            baos = new ByteArrayOutputStream();//内存分配字节数组；管道连接上 往里写内容
            dos = new DataOutputStream(baos);//因为不方便 Data包裹在外面 往外写
            dos.writeInt(x);
            dos.writeInt(y);
            dos.writeInt(dir.ordinal());//enum 看成数组 取下标
            dos.writeBoolean(moving);
            dos.writeInt(group.ordinal());
            dos.writeLong(id.getMostSignificantBits());//UUID 128位 高64位
            dos.writeLong(id.getLeastSignificantBits());
            dos.flush();//写进字节数组
            bytes = baos.toByteArray();//将内存字节数组转换为保存的字节数组
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
