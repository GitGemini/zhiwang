package com.hone.zhiwang.client;

import com.hone.zhiwang.application.ApplicationConstant;
import com.hone.zhiwang.application.ApplicationContext;
import com.hone.zhiwang.db.DBHelper;
import com.hone.zhiwang.download.DownloadHandler;
import com.hone.zhiwang.download.DownloadHandlerFactory;
import com.hone.zhiwang.keywords.KeywordStore;
import com.hone.zhiwang.request.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HandlerThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(HandlerThread.class);

    private final ApplicationContext context;

    public HandlerThread(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        RequestHandler requestHandler = context.getRequestHandler();
        KeywordStore keywordStore = context.getKeywordStore();
        DBHelper dbHelper = context.getDbHelper();
        while (requestHandler.hasNext()) {
            logger.info("请求参数：" + keywordStore.getCurrentKeys() + ", 字典文件的第：" + keywordStore.getNext() + "行, 查询到第" +
                    (requestHandler.getIndex() + 1) + "页");
            logger.info(String.format("文章总数：%s，有PDF下载方式的文章总数：%s，下载成功：%s，下载失败：%s", context.getTotalNum(), context.getPdfTotal(),
                    context.getSuccess(), context.getFail()));
            //获取概述页的URL
            Map<String, String> titleAndUrl = null;
            try {
                titleAndUrl = requestHandler.getNextOverview();
                if (titleAndUrl.isEmpty()) {
                    logger.info("概述页面为空");
                    // 重新加载
                    context.getRequestHandler().setIndex(context.getRequestHandler().getIndex() - 1);
                    context.setConnect(false);
                }
                for (Map.Entry<String, String> entry : titleAndUrl.entrySet()) {
                    logger.info("下载文件：" + entry.getKey());
                    //数据存在 跳过
                    if (dbHelper.existOfSuccess(entry.getKey())) {
                        logger.info("已下载");
                        continue;
                    }
                    if (dbHelper.existOfFail(entry.getKey())) {
                        logger.info("下载失败，等待重试！");
                        continue;
                    }
                    context.totalNumAdd();
                    try {
                        // 执行下载策略
                        context.getStrategyChain().doChain();
                        // 解析下载
                        DownloadHandlerFactory factory = context.getDownloadHandlerFactory();
                        DownloadHandler downloadHandler = factory.createHandler(context, entry.getKey(),
                                ApplicationConstant.HOST + entry.getValue());
                        downloadHandler.handler();
                        //获取成功，记录插入数据库
                        dbHelper.saveSuccess(entry.getValue(), entry.getKey());
                    } catch (Exception e) {
                        e.printStackTrace();
                        //失败的另外存放
                        //数据存在 跳过
                        if (dbHelper.existOfFail(entry.getKey())) {
                            continue;
                        }
                        dbHelper.saveFail(entry.getValue(), entry.getKey(), 1);
                    }
                }
            } catch (Exception e) {
                context.setConnect(false);
                e.printStackTrace();
            }
        }
        // 加载下一组关键字
        context.setLoadNext(true);
    }
}
