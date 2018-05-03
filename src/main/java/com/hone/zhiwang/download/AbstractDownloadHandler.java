package com.hone.zhiwang.download;

import com.hone.zhiwang.application.ApplicationContext;

public abstract class AbstractDownloadHandler implements DownloadHandler {

    private ApplicationContext context;

    private String title;

    private String url;

    public ApplicationContext getContext() {
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
