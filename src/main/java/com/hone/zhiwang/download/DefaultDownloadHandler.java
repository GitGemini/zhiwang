package com.hone.zhiwang.download;

import com.hone.zhiwang.application.ApplicationConstant;
import com.hone.zhiwang.parser.DataParser;
import com.hone.zhiwang.request.RequestHandler;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class DefaultDownloadHandler extends AbstractDownloadHandler {
    private static Logger logger = LoggerFactory.getLogger(DefaultDownloadHandler.class);

    /**
     * 下载文件
     */
    @Override
    public void handler() throws Exception {
        logger.info("开始解析下载地址...");
        RequestHandler requestHandler = getContext().getRequestHandler();
        String referer;
        //用文章URL建立连接
        HttpURLConnection connection = requestHandler.getHttpURLConnection(this.getUrl(), false, false);
        //获得文章的
        String uidv = connection.getHeaderField("Location");
        //handler是必须的URL，在第一个URL之前请求（可以认为这是第一个URL）
        logger.info("下载 handler");
//        String handlerUrlFormat = "/KRS/KRSWriteHandler.ashx?curUrl={1}&referUrl=http%3A//kns.cnki.net/kns/brief/brief.aspx%3Fpagename%3DASP.brief_default_result_aspx";
//        String handlerUrl = ApplicationConstant.HOST + handlerUrlFormat.replace("{1}", URLEncoder.encode(uidv, "UTF-8"));
//        connection = requestHandler.getHttpURLConnection(handlerUrl, false, true);
//        String content = DataParser.getContentOfHtml(connection);
        //文章的真实地址format
        String downloadHandler = "/KRS/KRSWriteHandler.ashx?curUrl=download.aspx%3FdbCode%3Dcjfq%26fileName%3D{1}&referUrl={2}&cnkiUserKey={3}&action=file&userName=&td={4}";
        String handler = ApplicationConstant.HOST + downloadHandler.replace("{1}", DataParser.getFileName(uidv))
                .replace("{2}", URLEncoder.encode(uidv, "UTF-8"))
                .replace("{3}", DataParser.getCnkiUserKey(requestHandler.getCookie()))
                .replace("{4}", System.currentTimeMillis() + "");
        connection = requestHandler.getHttpURLConnection(ApplicationConstant.HOST + uidv, false, true);
        referer = ApplicationConstant.HOST + uidv;
        String content = DataParser.getContentOfHtml(connection);
        //获得第一个URL
        String url = DataParser.getFirstUrl(content);
        if (url == null) {
            logger.error("文章：" + getTitle() + ",没有pdf下载方式！");
            return;
        }
        getContext().pdfTotalAdd();
//		//使用第一个URL建立连接获得第二个URL,禁止重定向
        connection = requestHandler.getHttpURLConnection(handler, false, false);
        content = DataParser.getContentOfHtml(connection);
        CloseableHttpClient httpclient = requestHandler.getHttpClient();
        HttpGet httpGet = requestHandler.httpGet(url);
        httpGet.setHeader("referer", referer);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        downLoadFromUrl(inputStream, getTitle() + ".pdf", getContext().getConfiguration().getPath());
    }

    private void downLoadFromUrl(InputStream inputStream, String fileName, String savePath) throws Exception {
        logger.info("文件:" + fileName + "  开始下载...");
        //获取自己数组
        byte[] bytes = readInputStream(inputStream);
        long size = bytes.length;
        //小于10kb意味下载失败
        if (size < getContext().getConfiguration().getFileMinSize()) {
            logger.info("文件:" + fileName + "  下载失败...  大小：" + size);
            getContext().failAdd();
            throw new RuntimeException("下载失败");
        }
        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        if (inputStream != null) {
            inputStream.close();
        }
        getContext().resize(size);
        getContext().successAdd();
        logger.info("文件:" + fileName + "  下载完成...");
    }

    /**
     * 从输入流中获取字节数组
     */
    private byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void printResponse(HttpResponse httpResponse) throws Exception {
        // 获取响应消息实体
        HttpEntity entity = httpResponse.getEntity();
        // 响应状态
        System.out.println("status:" + httpResponse.getStatusLine());
        System.out.println("headers:");
        HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext()) {
            System.out.println("\t" + iterator.next());
        }
        // 判断响应实体是否为空
        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            System.out.println("response length:" + responseString.length());
            System.out.println("response content:"
                    + responseString.replace("\r\n", ""));
        }
    }
}
