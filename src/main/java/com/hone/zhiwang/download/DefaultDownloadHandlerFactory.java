package com.hone.zhiwang.download;

import com.hone.zhiwang.application.ApplicationContext;

public class DefaultDownloadHandlerFactory implements DownloadHandlerFactory{

    @Override
    public DownloadHandler createHandler(ApplicationContext context, String title, String url) throws Exception {
        DefaultDownloadHandler handler = new DefaultDownloadHandler();
        handler.setContext(context);
        handler.setTitle(title);
        handler.setUrl(url);
        return handler;
    }
}
