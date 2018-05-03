package com.hone.zhiwang.strategy;

import com.hone.zhiwang.keywords.FileAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 根据下载的大小限制
 */
public class SizeLimitStrategy implements DownloadStrategy {
    private static Logger logger = LoggerFactory.getLogger(FileAdapter.class);

    /**
     * 睡眠次数
     */
    private long times = 0;

    @Override
    public void execute(StrategyChain chain) throws Exception {
        if (chain.getContext().getTotalSize() / 1000 / 1000 / 300 > times) {
            logger.info("下载数据量大小策略：每下300M睡眠20分钟");
            times++;
            Thread.sleep(1000 * 60 * 20);
            chain.getContext().getRequestHandler().updateCookie();
        }
        chain.doChain();
    }

    private long random() {
        Random random = new Random();
        return random.nextInt(10);
    }
}
