package org.tsingjyujing.spider_agency;

import com.google.gson.Gson;
import org.junit.Test;

import java.util.Map;
import java.util.function.Function;

public class RunAgencyTest {

    Function<String, String> println = (text) -> {
        System.out.println(text);
        return text;
    };

    @Test
    public void testVPNisOK() throws Exception {
        Gson gson = new Gson();
        String hostIP = "127.0.0.1";
        int hostPort = 8080;
        String url = "https://www.baidu.com/";
        String callURL = println.apply(String.format("http://%s:%d/agency/get?url=%s", hostIP, hostPort, CodecUtil.encodeString(url)));
        URLOpener ulo = new URLOpener(callURL, "get");
        Map data = gson.fromJson(println.apply(ulo.call()), Map.class);
        println.apply((String) data.get("status"));
        println.apply(CodecUtil.decodeString((String) data.get("data")));
    }


}