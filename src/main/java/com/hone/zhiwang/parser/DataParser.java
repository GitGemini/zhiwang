package com.hone.zhiwang.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据解析器
 */
public class DataParser {

    /**
     * @param connection  链接
     * @return HTML内容
     */
    public static String getContentOfHtml(HttpURLConnection connection) throws Exception{
        StringBuilder builder=new StringBuilder();
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String result = "";
        while ((result = reader.readLine()) != null) {
            builder.append(result);
        }
        reader.close();
        return builder.toString();
    }

    /**
     * @param content 网页内容
     * @return Map<url,标题> url不是文章的真正地址
     */
    public static Map<String, String> getTitleAndUrl(String content) {
        Map<String, String> map = new HashMap<String, String>();
        Document doc = Jsoup.parse(content);
        Elements elements = doc.select("a.fz14");
        for (Element element : elements) {
            String text = element.text();
            String url = element.attr("href");
            map.put(text, url);
        }
        return map;
    }

    /**
     * @param content
     * @return	页数
     */
    public static int getPages(String content){
        Document doc = Jsoup.parse(content);
        Elements element = doc.select("span.countPageMark");
        if (element.size() == 0){
            return 0;
        }
        String page = element.text();
        String[] array = page.split("/");
        return Integer.parseInt(array[1]);
    }


    /**
     * @param content	文本内容
     * @return	fileName
     */
    public static String getFileName(String content) {
        String result = "";
        Pattern pattern = Pattern.compile("(?<=filename=)[^&]*");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }


    /**
     * @param content	文本内容
     * @return	cnkiUserKey
     */
    public static String getCnkiUserKey(String content) {
        String result = null;
        Pattern pattern = Pattern.compile("(?<=cnkiUserKey=)[^;]*");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }

    /**
     * @param con  html内容
     * @return
     */
    public static String getFirstUrl(String content){
        Document doc = Jsoup.parse(content);
        Element element = doc.getElementById("pdfDown");
        String result = null;
        try {
            result = element.attr("href").trim();
        } catch (Exception e) {
        }
        return result;
    }
}
