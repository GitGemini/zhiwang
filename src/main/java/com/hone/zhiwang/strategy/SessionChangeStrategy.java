package com.hone.zhiwang.strategy;

import com.hone.zhiwang.keywords.FileAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 更新cookie
 */
public class SessionChangeStrategy implements DownloadStrategy {
    private static Logger logger = LoggerFactory.getLogger(FileAdapter.class);

    @Override
    public void execute(StrategyChain chain) throws Exception {
        if (chain.getContext().getTotalNum() % 30 == 0){
        logger.info("Cookie更换策略：每访问30篇更换...");
            chain.getContext().getRequestHandler().updateCookie();
        }
        chain.doChain();
    }
}
