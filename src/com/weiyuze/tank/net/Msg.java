package com.weiyuze.tank.net;

public abstract class Msg {
    //接口 形容词；抽象类 名词
    public abstract void handle();
    public abstract byte[] toBytes();
}
