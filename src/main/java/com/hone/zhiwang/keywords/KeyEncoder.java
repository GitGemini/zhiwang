package com.hone.zhiwang.keywords;

import java.net.URLEncoder;

public class KeyEncoder {

    /**
     * @param args 关键字数组，可以是多个（随意），都是AND关系
     * @throws Exception
     * @return URL中的Value
     */
    public static String encode(String[] args) throws Exception {
        StringBuilder result = new StringBuilder();
        int flag = 0;
        for (int i = 0; i < args.length; i++) {
            flag = (i + 2) / 2;
            if (i % 2 == 0) {
                result.append("&txt_" + flag + "_sel=SU");
            }
            if (i % 2 == 0) {
                result.append("&txt_" + flag + "_value1=" + URLEncoder.encode(args[i], "UTF-8"));
            } else {
                result.append("&txt_" + flag + "_value2=" + URLEncoder.encode(args[i], "UTF-8"));
            }
            if (i % 2 == 1) {
                if (i > 1) {
                    result.append("&txt_" + flag + "_logical=and");
                }
                result.append("&txt_" + flag + "_relation=%23CNKI_AND&txt_" + flag + "_special1=%3D");
            }
        }
        return result.toString();
    }
}
