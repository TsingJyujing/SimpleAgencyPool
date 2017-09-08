package org.tsingjyujing.spider_agency;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.Callable;

public class URLOpener implements Callable<String> {
    String url;
    String method;

    public URLOpener(String url, String method) {
        this.url = url;
        this.method = method;
    }

    @Override
    public String call() throws Exception {
        return openURL(url, method);
    }

    private String openURL(String url, String method) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpRequestBase httpRequestBase;
            switch (method.toLowerCase()) {
                case "get":
                    httpRequestBase = new HttpGet(url);
                    break;
                case "post":
                    httpRequestBase = new HttpPost(url);
                    break;
                default:
                    httpRequestBase = new HttpGet(url);
                    break;
            }
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        return EntityUtils.toString(entity);
                    } else {
                        throw new NullPointerException("Get null entity.");
                    }
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            return httpclient.execute(httpRequestBase, responseHandler);
        }
    }
}
