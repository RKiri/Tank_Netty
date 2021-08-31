package test;

import com.weiyuze.tank.net.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;
import com.weiyuze.tank.Dir;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TankStartMovingMsgCodecTest {

    @Test
    void testEncoder() {
        EmbeddedChannel ch = new EmbeddedChannel();

        UUID id = UUID.randomUUID();
        TankStartMovingMsg msg = new TankStartMovingMsg(id,5,10, Dir.LEFT);
        ch.pipeline().addLast(new MsgEncoder());

        ch.writeOutbound(msg);

        ByteBuf buf = ch.readOutbound();
        MsgType msgType = MsgType.values()[buf.readInt()];
        assertEquals(MsgType.TankStartMoving,msgType);

        int length = buf.readInt();
        assertEquals(28,length);

        UUID uuid = new UUID(buf.readLong(),buf.readLong());
        int x = buf.readInt();
        int y = buf.readInt();
        Dir dir = Dir.values()[buf.readInt()];

        assertEquals(id,uuid);
        assertEquals(5,x);
        assertEquals(10,y);
    }

    @Test
    void testDecoder() {
        EmbeddedChannel ch = new EmbeddedChannel();

        UUID id = UUID.randomUUID();
        TankStartMovingMsg msg = new TankStartMovingMsg(id,5,10,Dir.LEFT);
        ch.pipeline().addLast(new MsgDecoder());

         ByteBuf buf = Unpooled.buffer();
         buf.writeInt(MsgType.TankStartMoving.ordinal());
         byte[] bytes = msg.toBytes();
         buf.writeInt(bytes.length);
         buf.writeBytes(bytes);

         ch.writeInbound(buf.duplicate());

         TankStartMovingMsg msgM = ch.readInbound();

         assertEquals(5,msgM.getX());
         assertEquals(10,msgM.getY());
         assertEquals(Dir.LEFT,msgM.getDir());
         assertEquals(id,msgM.getId());
    }


}