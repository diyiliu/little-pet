import org.junit.Test;

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
        str = "[ZJ*589468010000246**0001*0005*GPS,1]";

        str = " GPS, ONOFF";
    }
}
