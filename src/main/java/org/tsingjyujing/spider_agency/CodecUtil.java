package org.tsingjyujing.spider_agency;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;

/**
 * Created by Yuan Yifan on 2017/8/9.
 */
public class CodecUtil {

    public static String encodeString(String obj) throws IOException {
        return new String(Base64.encodeBase64(obj.getBytes()));
    }


    public static String decodeString(String info) throws IOException {
        return new String(Base64.decodeBase64(info.getBytes()));
    }
}
