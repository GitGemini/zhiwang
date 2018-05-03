package com.hone.zhiwang.application;

import com.hone.zhiwang.db.DBHelper;
import com.hone.zhiwang.keywords.KeywordStore;
import com.hone.zhiwang.parser.ConfigurationParser;
import com.hone.zhiwang.download.DownloadHandlerFactory;
import com.hone.zhiwang.request.RequestHandler;
import com.hone.zhiwang.strategy.StrategyChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationContext {
    private static Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

    private ApplicationConfiguration configuration;

    private RequestHandler requestHandler;

    private StrategyChain strategyChain;

    private DownloadHandlerFactory downloadHandlerFactory;

    private DBHelper dbHelper;

    private String cookie;

    private KeywordStore keywordStore;

    private long totalSize;

    private volatile boolean isConnect;

    /**
     * 标记是否加载下一组关键字
     */
    private volatile boolean loadNext;

    /**
     * 文章总数
     */
    private int totalNum;

    private int pdfTotal;

    private int success;

    private int fail;

    public void init() throws Exception{
        logger.info("初始化Application...");
        ConfigurationParser parser = new ConfigurationParser();
        configuration = parser.parser();
        logger.info("初始化KeyStore...");
        keywordStore = new KeywordStore(configuration.getStoreAdapter());
        keywordStore.init();
        keywordStore.setNext(configuration.getKeyIndex() - 1);
        logger.info("初始化下载策略...");
        strategyChain = new StrategyChain(this);
        strategyChain.addAll(configuration.getStrategies());
        logger.info("初始化DBHelper...");
        dbHelper = configuration.getDbHelper();
        dbHelper.init();
        downloadHandlerFactory = configuration.getDownloadHandlerFactory();
    }

    public ApplicationConfiguration getConfiguration() {
        return configuration;
    }

    public StrategyChain getStrategyChain() {
        return strategyChain;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie() {
        return cookie;
    }

    public RequestHandler getRequestHandler() {
        if (requestHandler == null) {
            requestHandler = new RequestHandler(this);
        }
        return requestHandler;
    }

    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public void resize(long size) {
        this.totalSize += size;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public KeywordStore getKeywordStore() {
        return keywordStore;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void totalNumAdd() {
        this.totalNum++;
    }

    public int getPdfTotal() {
        return pdfTotal;
    }

    public void pdfTotalAdd() {
        this.pdfTotal++;
    }

    public int getSuccess() {
        return success;
    }

    public void successAdd() {
        this.success++;
    }

    public int getFail() {
        return fail;
    }

    public void failAdd() {
        this.fail++;
    }

    public boolean isLoadNext() {
        return loadNext;
    }

    public void setLoadNext(boolean loadNext) {
        this.loadNext = loadNext;
    }

    public DBHelper getDbHelper() {
        return dbHelper;
    }

    public DownloadHandlerFactory getDownloadHandlerFactory() {
        return downloadHandlerFactory;
    }
}
