package com.hone.zhiwang.download;

import com.hone.zhiwang.application.ApplicationContext;

public interface DownloadHandlerFactory {
    DownloadHandler createHandler(ApplicationContext context, String title, String url) throws Exception;
}
