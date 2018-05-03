package com.hone.zhiwang.strategy;

public interface DownloadStrategy {
    void execute(StrategyChain chain) throws Exception;
}
