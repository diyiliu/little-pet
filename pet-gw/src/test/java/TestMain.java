import com.diyiliu.plugin.util.CommonUtil;
import com.diyiliu.plugin.util.DateUtil;
import com.diyiliu.plugin.util.GpsCorrectUtil;
import com.dyl.gw.support.jpa.dto.PetGpsCur;
import com.dyl.gw.support.jpa.dto.PetGps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.*;

/**
 * Description: TestMain
 * Author: DIYILIU
 * Update: 2018-07-06 15:22
 */
public class TestMain {


    @Test
    public void test() {
        String str = "[ZJ*589468010000246*0019*002c*INIT,FFFFFFFFFFF,1,S1_C2_CN_V1.1.1,0000,0000]";


        System.out.println(str.length());

        System.out.println("INIT,FFFFFFFFFFF,1,S1_C2_CN_V1.1.1,0000,0000".length());


        // 初始化应答
        str = "[ZJ*589468010000246*0001*0006*INIT,1]";

        // 轮询
        str = "[ZJ*589468010000246*0001*0002*CR]";

        // 开启GPS
        str = "[ZJ*589468010000246*0001*0005*GPS,1]";


        str = "[ZJ*589468010000246*0001*0016*LK,2015-08-18,14:30:30]";

        str = " GPS, ONOFF";
    }


    @Test
    public void test1() {
        String str = "[ZJ*589468010000246*0019*002c*INIT,FFFFFFFFFFF,1,S1_C2_CN_V1.1.1,0000,0000]";

        ByteBuf buf = Unpooled.copiedBuffer(str.getBytes());

        byte[] bytes = new byte[buf.readableBytes() - 31];
        buf.getBytes(30, bytes);


        str = new String(bytes);

        System.out.println(str.split(",")[0]);
    }

    @Test
    public void test2() {
        String str = "*0019*002c*";
        String[] strArray = str.split("\\*");

        System.out.println(strArray[1]);
    }


    @Test
    public void test3() {
        String str = "BDBDBDBDA100504E0A6D775E024E0A6D775E026D664E1C65B0533A67689AD84E2D8DE" +
                "F002F79BB745E6D6695E88BCA90E87EA6003600367C73002F79BB534E7F8E8FBE592791525E9" +
                "7505C8F66573A7EA6003800377C73";
        byte[] bytes = CommonUtil.hexStringToBytes(str);

//        System.out.println(String.format("%X", CommonUtil.checkSum(bytes)));

        str = "4E0A6D775E024E0A6D775E026D664E1C65B0533A67689AD84E2D8DE" +
                "F002F79BB745E6D6695E88BCA90E87EA6003600367C73002F79BB534E7F8E8FBE592791525E9" +
                "7505C8F66573A7EA6003800377C73";

        bytes = CommonUtil.hexStringToBytes(str);
        System.out.println(new String(bytes));
    }


    @Test
    public void test4() {
        String str = "CF F2 79 BE 04 11 03 00".replace(" ", "");
        byte[] bytes = CommonUtil.hexStringToBytes(str);

        bytes = CommonUtil.bytesReverse(bytes);

        System.out.println(CommonUtil.bytesToLong(bytes));
    }

    @Test
    public void test5() {
        String str = "28D4DE55F186BDA100";
        //str = "BD BD BD BD F0 CF F2 79 BE 04 11 03 00 00 00".replace(" ", "");
        byte[] bytes = CommonUtil.hexStringToBytes(str);

        System.out.println(String.format("%02X", CommonUtil.checkSum(bytes)));
    }


    @Test
    public void test6() {
        String str = "28D4DE55";
        byte[] bytes = CommonUtil.hexStringToBytes(str);

        long t = CommonUtil.bytesToLong(bytes);

        System.out.println(DateUtil.dateToString(new Date(t)));

        Date now = new Date();
        t = Long.valueOf(now.getTime()).intValue();

        System.out.println(DateUtil.dateToString(new Date(t)));
    }


    /**
     * 登录应答
     */
    @Test
    public void test7() {
        Date now = new Date();
        int time = Long.valueOf(now.getTime()).intValue();
        int code = now.hashCode();

        ByteBuf buf = Unpooled.buffer(10);
        buf.writeInt(time);
        buf.writeByte(0xF1);
        buf.writeInt(code);


        byte[] bytes = new byte[9];
        buf.readBytes(bytes);

        byte check = CommonUtil.checkSum(bytes);
        buf.writeByte(check);

        bytes = buf.array();

        System.out.println(CommonUtil.bytesToStr(bytes));
    }


    @Test
    public void test8() {
        Date now = new Date();
        int time = Long.valueOf(now.getTime()).intValue();

        ByteBuf buf = Unpooled.buffer(22);
        //buf.writeInt(time);
        buf.writeBytes(new byte[]{(byte) 0xBD, (byte) 0xBD, (byte) 0xBD, (byte) 0xBD});
        buf.writeByte(0xA3);
        buf.writeLong(0l);
        buf.writeLong(0l);


        byte[] bytes = new byte[21];
        buf.readBytes(bytes);

        byte check = CommonUtil.checkSum(bytes);
        buf.writeByte(check);

        bytes = buf.array();

        System.out.println(CommonUtil.bytesToStr(bytes));
    }


    @Test
    public void test9(){
        double lat = 34.2873991;
        double lng = 117.264987;

/*
        lat = 34.2828852;
        lng = 117.2599542;
*/

        double[] location = GpsCorrectUtil.gcj02_To_Gps84(lat, lng);

        System.out.println(location[0] + "," + location[1]);

    }

    @Test
    public void test10(){

        PetGps petGps = new PetGps();
        petGps.setSpeed(1.0);

        PetGpsCur curGps = (PetGpsCur) petGps;
        System.out.println(curGps.getSpeed());
    }
}
