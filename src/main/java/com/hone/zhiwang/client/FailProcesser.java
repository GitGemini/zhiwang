package com.hone.zhiwang.client;

import com.hone.zhiwang.application.ApplicationConstant;
import com.hone.zhiwang.application.ApplicationContext;
import com.hone.zhiwang.db.DBHelper;
import com.hone.zhiwang.download.AbstractDownloadHandler;
import com.hone.zhiwang.download.DefaultDownloadHandler;
import com.hone.zhiwang.download.DownloadHandler;
import com.hone.zhiwang.download.DownloadHandlerFactory;
import com.hone.zhiwang.request.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FailProcesser {
    private static Logger logger = LoggerFactory.getLogger(FailProcesser.class);

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ApplicationContext();
        context.init();
        DBHelper dbHelper = context.getDbHelper();
        dbHelper.init();
        List<String> record = dbHelper.select();
        while (record != null && record.size() > 0) {
            if (!context.isConnect()) {
                logger.info("重新连接");
                context.getRequestHandler().testConnection();
                RequestHandler requestHandler = new RequestHandler(context);
                requestHandler.init();
            }
            if (Integer.parseInt(record.get(3)) >= 10) {
                record = dbHelper.select();
                continue;
            }
            try {
                DownloadHandlerFactory factory = context.getDownloadHandlerFactory();
                DownloadHandler downloadHandler = factory.createHandler(context, record.get(2),
                        ApplicationConstant.HOST + record.get(1));
                downloadHandler.handler();
                dbHelper.saveSuccess(record.get(1), record.get(2));
//                dbHelper.deleteFail(Integer.parseInt(record.get(0)));
            } catch (Exception e) {
                e.printStackTrace();
//                dbHelper.saveFail(record.get(1), record.get(2), Integer.parseInt(record.get(3)) + 1);
//                dbHelper.deleteFail(Integer.parseInt(record.get(0)));
            }
            record = dbHelper.select();
        }
        logger.info("完成");
    }
}