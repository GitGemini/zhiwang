package com.hone.zhiwang.strategy;

import com.hone.zhiwang.keywords.FileAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class RandomStrategy implements DownloadStrategy {
    private static Logger logger = LoggerFactory.getLogger(FileAdapter.class);

    @Override
    public void execute(StrategyChain chain) throws Exception {
        Random random = new Random();
        long time = random.nextInt(15);
        logger.info("随机策略：睡眠" + time + "s");
        Thread.sleep(time * 1000);
        chain.doChain();
    }

}
