package com.weiyuze.tank;

import com.weiyuze.tank.net.Client;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        TankFrame tf = TankFrame.INSTANCE;
        tf.setVisible(true);

        /*int initTankCount = Integer.parseInt((String)PropertyMgr.get("initTankCount"));

        //初始化敌方坦克
        for (int i = 0; i < initTankCount; i++) {
            tf.tanks.add(new Tank(50 + i * 80, 200, Dir.DOWN, Group.BAD, tf));
        }*/

        //new Thread(()->new Audio("audio/war1.wav").loop()).start();

        //用单独的线程重画 否则下面执行不了
        new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tf.repaint();
            }
        }).start();

        //在主线程做网络连接
        Client c = new Client();
        c.connect();

    }
}
