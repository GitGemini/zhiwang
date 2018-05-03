package com.hone.zhiwang.strategy;

import com.hone.zhiwang.keywords.FileAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * 每天7点开始
 */
public class BeginOfDayStrategy implements DownloadStrategy {
    private static Logger logger = LoggerFactory.getLogger(FileAdapter.class);

    @Override
    public void execute(StrategyChain chain) throws Exception {
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (h < 7) {
            logger.info("限时开始策略：睡眠7小时");
            Thread.sleep(1000 * 60 * 60 * 7 + 300000);
            chain.getContext().getRequestHandler().updateCookie();
        }
        chain.doChain();
    }
}
