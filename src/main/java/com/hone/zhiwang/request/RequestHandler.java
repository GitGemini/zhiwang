package com.hone.zhiwang.request;

import com.hone.zhiwang.application.ApplicationConstant;
import com.hone.zhiwang.application.ApplicationContext;
import com.hone.zhiwang.parser.DataParser;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 请求工具类
 */
public class RequestHandler {
    private static Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private CloseableHttpClient httpClient = HttpClients.custom().build();

    private ApplicationContext context;

    private String cookie;

    private int pageNum;

    private int index;

    private String scope;

    private String param;

    public RequestHandler(ApplicationContext context) {
        this.context = context;
    }

    public void init() {
        logger.info("初始化RequestHandler...  请求参数：" + context.getKeywordStore().getNextKeys() + "字典文件所在行：" +
                (context.getKeywordStore().getNext()));
        this.scope = context.getConfiguration().getScope();
        this.param = context.getKeywordStore().next();
        if (context.getTotalNum() == 0) {
            this.index = context.getConfiguration().getPageIndex() - 1;
        }
        try {
            // 获取cookie
            this.updateCookie();
//            this.login();
            String handler = getSearchHandler();
            HttpURLConnection connection = getHttpURLConnection(handler, true, true);
            DataParser.getContentOfHtml(connection);
            // 获取页数
            loadPageNum();
            context.setRequestHandler(this);
        } catch (Exception e) {
            logger.error("请求处理器初始化失败");
            e.printStackTrace();
        }
    }

    /**
     * url 添加请求头
     *
     * @param url        连接
     * @param flag       区分不同的链接   true是searchHandler
     * @param isRedirect 是否允许重定向
     */
    @SuppressWarnings("static-access")
    public HttpURLConnection getHttpURLConnection(String url, Boolean flag, Boolean isRedirect) throws Exception {
//        logger.info(url);
        //禁止重定向
        HttpURLConnection.setFollowRedirects(isRedirect);
        if (cookie == null) {
            updateCookie();//获得Cookie
        }
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(8 * 1000);
        conn.setDoOutput(true);
        conn.setRequestMethod("GET");
        if (flag) {
            conn.setRequestProperty("Accept", "*/*");
        } else {
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        }
        conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
        conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Cookie", cookie);
        conn.setRequestProperty("Host", ApplicationConstant.HOST);
        conn.setRequestProperty("Referer",
                "http://kns.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0");
        if (!flag) {
            conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
        }
        return conn;
    }

    /**
     * 获得Cookie信息
     */
    public void updateCookie() throws IOException {
        URL url = new URL("http://kns.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        Map<String, List<String>> map = connection.getHeaderFields();
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            for (String value : entry.getValue()) {
                //截取有用的值
                value = (value.split(";"))[0];
                if ("Set-Cookie".equals(entry.getKey())) {
                    if (!value.contains("Ecp_IpLoginFail=")) {
                        //拼接Cookie信息
                        builder.append(value);
                        builder.append(";");
                    } else {
                        String[] split = value.split("=");
                        if (split.length > 1) {
                            this.cookie = null;
                            context.setCookie(null);
                            return;
                        }
                    }
                }
            }
        }
        String string = "RsPerPage=20;cnkiUserKey=1d342f81-b520-d347-a5ae-5a91d4e40765;KNS_DisplayModel=custommode@SCDB;SID_kredis=125142";
        builder.append(string);
        this.cookie = builder.toString();
        context.setCookie(this.cookie);
    }

    public void login() throws Exception{
        String loginUrl = "http://login.cnki.net/TopLogin/api/loginapi/UidLogin?uid=WEEvREcwSlJHSldRa1FhcTdWajFtOTU4N204VTEwRzVYZDN2SGVrY0lFQT0=$9A4hF_YAuvQ5obgVAqNKPCYcEjKensW4ggI8Fm4gTkoUKaID8j8gFw!!&cookieLogin=true&callback=jQuery1113011345428211175612_1524314534861&_=1524314534862";
        HttpURLConnection connection = getHttpURLConnection(loginUrl, false, true);
        String html = DataParser.getContentOfHtml(connection);
        logger.info(html);
    }

    /**
     * 先执行update
     *
     * @return 页数
     * @throws Exception
     */
    private void loadPageNum() throws Exception {
        String url = "http://kns.cnki.net/kns/brief/brief.aspx?pagename=ASP.brief_result_aspx&dbPrefix=SCDB&dbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&research=off&t=1488776209941&keyValue=%e8%82%9d%e7%99%8c%e6%97%a9%e6%9c%9f%e8%af%8a%e6%96%ad&S=1&DisplayMode=listmode";
        HttpURLConnection connection = getHttpURLConnection(url, false, true);
        String html = DataParser.getContentOfHtml(connection);
        this.pageNum = DataParser.getPages(html);
        logger.info("总页数为：" + this.pageNum);
        if (this.pageNum == 0){
            Thread.sleep(5000);
            init();
        }
    }

    /**
     * 下一页概述信息
     */
    public Map<String, String> getNextOverview() throws Exception {
        logger.info("获取概述页信息");
        //获取概述页的URL
        index++;
        String url = "http://kns.cnki.net/kns/brief/brief.aspx?curpage=" + index +
                "&RecordsPerPage=20&QueryID=20&ID=&turnpage=1&tpagemode=L&dbPrefix=SCDB&Fields=" +
                "&DisplayMode=listmode&PageName=ASP.brief_result_aspx";
        HttpURLConnection connection = getHttpURLConnection(url, false, true);
        //获得概述页内容
        String html = DataParser.getContentOfHtml(connection);
        //获得URL和标题
        Map<String, String> titleAndUrl = DataParser.getTitleAndUrl(html);
        for (Map.Entry<String, String> entry : titleAndUrl.entrySet()) {
            logger.info("标题：" + entry.getKey());
            logger.info("URL：" + entry.getValue());
        }
        return titleAndUrl;
    }

    public HttpGet httpGet(String url) {
        HttpGet httpGet = new HttpGet(url);

        //設置httpGet的头部參數信息

        httpGet.setHeader("Accept", "*/*");

        httpGet.setHeader("Accept-Charset", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        httpGet.setHeader("Accept-Encoding", "gzip, deflate");

        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");

        httpGet.setHeader("Connection", "keep-alive");

        httpGet.setHeader("Cookie", cookie);

        httpGet.setHeader("Host", "kns.cnki.net");

        httpGet.setHeader("Referer", "http://kns.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");

        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0");

        return httpGet;
    }

    /**
     * 测试连接
     */
    public void testConnection() throws Exception {
        logger.info("测试连接！");
        //用于测试连接的URL
        URL url = new URL(
                "http://kns.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.getInputStream();
        connection.disconnect();
        context.setConnect(true);
        logger.info("连接成功！");
    }

    /**
     * @return 锁定搜索范围的URL
     */
    private String getSearchHandler() {
        String searchHandler = null;
        switch (this.scope) {
        case "主题":
            searchHandler =
                    "http://kns.cnki.net/kns/request/SearchHandler.ashx?action=&NaviCode=E&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND" +
                            this.param + "&his=0";
            break;

        case "篇名":
            searchHandler =
                    "http://kns.cnki.net/kns/request/SearchHandler.ashx?action=&NaviCode=E&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND" +
                            param.replace("sel=SU", "sel=TI") + "&his=0";
            break;

        case "关键字":
            searchHandler =
                    "http://kns.cnki.net/kns/request/SearchHandler.ashx?action=&NaviCode=E&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND" +
                            param.replace("sel=SU", "sel=KY") + "&his=0";
            break;

        case "摘要":
            searchHandler =
                    "http://kns.cnki.net/kns/request/SearchHandler.ashx?action=&NaviCode=E&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND" +
                            param.replace("sel=SU", "sel=AB") + "&his=0";
            break;

        case "全文":
            searchHandler =
                    "http://kns.cnki.net/kns/request/SearchHandler.ashx?action=&NaviCode=E&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND" +
                            param.replace("sel=SU", "sel=FT") + "&his=0";
            break;

        case "参考文献":
            searchHandler =
                    "http://kns.cnki.net/kns/request/SearchHandler.ashx?action=&NaviCode=E&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND" +
                            param.replace("sel=SU", "sel=RF") + "&his=0";
            break;

        case "中国分类号":
            searchHandler =
                    "http://kns.cnki.net/kns/request/SearchHandler.ashx?action=&NaviCode=E&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND" +
                            param.replace("sel=SU", "sel=CLC%24%3D%7C%3F") + "&his=0";
            break;

        default:
            searchHandler =
                    "http://kns.cnki.net/kns/request/SearchHandler.ashx?action=&NaviCode=E&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND" +
                            param + "&his=0";
            break;
        }
        return searchHandler;
    }

    public boolean hasNext() {
        return index < pageNum;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public int getPageNum() {
        return pageNum;
    }

    public String getCookie() {
        return cookie;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }
}
