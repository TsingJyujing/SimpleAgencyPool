package org.tsingjyujing.spider_agency;

import com.google.gson.Gson;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Controller
@EnableAutoConfiguration
public class AgencyApplication {

    public static final int MAX_PARARLLEL_REQUEST = 30;
    ExecutorService URLReaderPool;

    public AgencyApplication() {
        URLReaderPool = Executors.newFixedThreadPool(MAX_PARARLLEL_REQUEST);
    }


    @RequestMapping(value = "/agency/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String getAgencyData(
            @RequestParam(name = "url") String url
    ) {
        Map<String, String> responseData = new HashMap<>();
        try {
            url = CodecUtil.decodeString(url);
            System.out.printf("Trying to get %s from web...\n",url);
            Future<String> futureResponse = URLReaderPool.submit(new URLOpener(url, "get"));
            String response = futureResponse.get();
            responseData.put("data", CodecUtil.encodeString(response));
            responseData.put("status", "success");
            System.out.printf("Get %s from web successfully\n",url);
        } catch (Exception e) {
            responseData.clear();
            responseData.put("status", "fail");
            responseData.put("reason", e.getMessage());
            System.out.printf("Get %s from web failed.\n",url);
        }
        return new Gson().toJson(responseData, Map.class);
    }


}
