import com.diyiliu.plugin.util.JacksonUtil;
import com.dyl.gw.support.model.GdLocation;
import org.junit.Test;

/**
 * Description: TestJson
 * Author: DIYILIU
 * Update: 2018-07-12 14:10
 */
public class TestJson {


    @Test
    public void test() throws Exception{
        String str = "{" +
                "    \"infocode\": \"10000\"," +
                "    \"result\": {" +
                "        \"city\": \"徐州市\"," +
                "        \"province\": \"江苏省\"," +
                "        \"poi\": \"诺万科技大厦\"," +
                "        \"adcode\": \"320305\"," +
                "        \"street\": \"杨山路\"," +
                "        \"desc\": \"江苏省 徐州市 贾汪区 杨山路 靠近诺万科技大厦\"," +
                "        \"country\": \"中国\"," +
                "        \"type\": \"3\"," +
                "        \"location\": \"117.2649875,34.2873991\"," +
                "        \"road\": \"杨山路\"," +
                "        \"radius\": \"100\"," +
                "        \"citycode\": \"0516\"" +
                "    }," +
                "    \"info\": \"OK\"," +
                "    \"status\": \"1\"" +
                "}";

        GdLocation location = JacksonUtil.toObject(str, GdLocation.class);
        System.out.println(location);
    }
}
