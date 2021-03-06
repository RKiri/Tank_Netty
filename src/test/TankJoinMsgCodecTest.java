package test;

import com.weiyuze.tank.net.MsgType;
import com.weiyuze.tank.net.TankJoinMsg;
import com.weiyuze.tank.net.MsgDecoder;
import com.weiyuze.tank.net.MsgEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;
import com.weiyuze.tank.Dir;
import com.weiyuze.tank.Group;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TankJoinMsgCodecTest {
    //Codec和单元测试均有误时 有可能会通过

    @Test
    void testEncoder() {
        EmbeddedChannel ch = new EmbeddedChannel();


        UUID id = UUID.randomUUID();
        TankJoinMsg msg = new TankJoinMsg(5, 10, Dir.DOWN, true, Group.BAD, id);
        ch.pipeline()
                .addLast(new MsgEncoder());

        ch.writeOutbound(msg);

        ByteBuf buf = (ByteBuf) ch.readOutbound();

        MsgType msgType = MsgType.values()[buf.readInt()];
        assertEquals(MsgType.TankJoin, msgType);

        int length = buf.readInt();
        assertEquals(33,length);

        int x = buf.readInt();
        int y = buf.readInt();
        int dirOrdinal = buf.readInt();
        Dir dir = Dir.values()[dirOrdinal];
        boolean moving = buf.readBoolean();
        int groupOrdinal = buf.readInt();
        Group g = Group.values()[groupOrdinal];
        UUID uuid = new UUID(buf.readLong(), buf.readLong());

        assertEquals(5, x);
        assertEquals(10, y);
        assertEquals(Dir.DOWN, dir);
        assertEquals(true, moving);
        assertEquals(Group.BAD, g);
        assertEquals(id, uuid);
    }

    @Test
    void testDecoder() {
        EmbeddedChannel ch = new EmbeddedChannel();

        UUID id = UUID.randomUUID();
        TankJoinMsg msg = new TankJoinMsg(5, 10, Dir.DOWN, true, Group.BAD, id);

        ch.pipeline().addLast(new MsgDecoder());

        ByteBuf buf = Unpooled.buffer();

        buf.writeInt(MsgType.TankJoin.ordinal());
        byte[] bytes = msg.toBytes();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);

        ch.writeInbound(buf.duplicate());//拷贝一个新对象，在新对象上修改不会影响前对象

        TankJoinMsg msgD = ch.readInbound();

        assertEquals(5, msgD.x);
        assertEquals(10, msgD.y);
        assertEquals(Dir.DOWN, msgD.dir);
        assertEquals(true, msgD.moving);
        assertEquals(Group.BAD, msgD.group);
        assertEquals(id, msgD.id);


    }


}