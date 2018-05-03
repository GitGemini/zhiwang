package com.hone.zhiwang.application;

import com.hone.zhiwang.db.DBHelper;
import com.hone.zhiwang.keywords.StoreAdapter;
import com.hone.zhiwang.download.DownloadHandlerFactory;
import com.hone.zhiwang.strategy.DownloadStrategy;

import java.util.List;

public class ApplicationConfiguration {

    /**
     * 文件下载路径
     */
    private String path;

    private long fileMinSize;

    private String scope;

    private int keyIndex;

    private int pageIndex;

    private List<DownloadStrategy> strategies;

    private StoreAdapter storeAdapter = null;

    private DBHelper dbHelper = null;

    private DownloadHandlerFactory downloadHandlerFactory = null;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getFileMinSize() {
        return fileMinSize;
    }

    public void setFileMinSize(long fileMinSize) {
        this.fileMinSize = fileMinSize;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getKeyIndex() {
        return keyIndex;
    }

    public void setKeyIndex(int keyIndex) {
        this.keyIndex = keyIndex;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setStrategies(List<DownloadStrategy> strategies) {
        this.strategies = strategies;
    }

    public List<DownloadStrategy> getStrategies() {
        return strategies;
    }

    public StoreAdapter getStoreAdapter() {
        return storeAdapter;
    }

    public void setStoreAdapter(StoreAdapter storeAdapter) {
        this.storeAdapter = storeAdapter;
    }

    public DBHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public DownloadHandlerFactory getDownloadHandlerFactory() {
        return downloadHandlerFactory;
    }

    public void setDownloadHandlerFactory(DownloadHandlerFactory downloadHandlerFactory) {
        this.downloadHandlerFactory = downloadHandlerFactory;
    }

    @Override
    public String toString() {
        return "ApplicationConfiguration{" +
                "path='" + path + '\'' +
                ", fileMinSize=" + fileMinSize +
                ", scope='" + scope + '\'' +
                ", keyIndex=" + keyIndex +
                ", pageIndex=" + pageIndex +
                ", strategies=" + strategies +
                ", storeAdapter=" + storeAdapter +
                '}';
    }
}
