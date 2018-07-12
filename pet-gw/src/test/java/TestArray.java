import org.junit.Test;

import java.util.Arrays;

/**
 * Description: TestArray
 * Author: DIYILIU
 * Update: 2018-07-12 16:23
 */
public class TestArray {

    @Test
    public void test() {

        String str = "UD,060718,023021,A,34.288593,N,117.259452,E,0,0,0,4,100,50,1000,50,00000000,6,255,460,0,21238,13006,45,21238,10920,54,21238,19102,47,21238,19367,44,21238,13007,41,21238,10922,38";
        String[] msgArray = str.split(",");

        int btsCount = Integer.valueOf(msgArray[17]);
        int to = 19 + 2 + 3 * btsCount;
        String[] btsArray = Arrays.copyOfRange(msgArray, 19, to);

        System.out.println(btsArray.length);
    }
}
