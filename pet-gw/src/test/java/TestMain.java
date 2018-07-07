import com.diyiliu.plugin.util.DateUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import sun.swing.SwingUtilities2;

import java.util.Date;

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
    public void test1(){
        String str = "[ZJ*589468010000246*0019*002c*INIT,FFFFFFFFFFF,1,S1_C2_CN_V1.1.1,0000,0000]";

        ByteBuf buf = Unpooled.copiedBuffer(str.getBytes());

        byte[] bytes = new byte[buf.readableBytes() - 31];
        buf.getBytes(30, bytes);


        str = new String(bytes);

        System.out.println(str.split(",")[0]);
    }

    @Test
    public void test2(){
        String str = "*0019*002c*";
        String[] strArray = str.split("\\*");

        System.out.println(strArray[1]);
    }


    @Test
    public void test3(){
        String str = DateUtil.dateToString(new Date());

        System.out.println(str.replace(" ", ","));
    }
}
