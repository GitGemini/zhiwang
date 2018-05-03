package com.hone.zhiwang.client;

import com.hone.zhiwang.application.ApplicationContext;
import com.hone.zhiwang.keywords.KeywordStore;
import com.hone.zhiwang.request.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class Client {
    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ApplicationContext();
        context.init();
        KeywordStore keywordStore = context.getKeywordStore();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                //断开连接才进行测试
                if ((!context.isConnect()) || context.isLoadNext()) {
                    try {
                        RequestHandler requestHandler = context.getRequestHandler();
                        if (!context.isLoadNext()) {
                            requestHandler.testConnection();
                        } else {
                            context.setLoadNext(false);
                        }
                        //连接成功  循环获取内容
                        if (keywordStore.hasNext()) {
                            if (requestHandler.hasNext()) {
                                requestHandler.updateCookie();
                            } else {
                                requestHandler = new RequestHandler(context);
                                requestHandler.init();
                            }
                            new Thread(new HandlerThread(context)).start();
                        } else {
                            logger.info("完成");
                            timer.cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.info("连接失败！");
                        //标志置为false
                        context.setConnect(false);
                    }
                } else {
                    logger.info("连接正常！");
                }
            }
        };
        //30秒检测一次
        timer.schedule(task, 0, 60 * 1000);
    }
}
