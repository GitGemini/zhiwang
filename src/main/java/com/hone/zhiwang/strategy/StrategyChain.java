package com.hone.zhiwang.strategy;

import com.hone.zhiwang.application.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class StrategyChain {
    private ApplicationContext context;

    private List<DownloadStrategy> list = new ArrayList<>();

    private int index = 0;

    public StrategyChain(ApplicationContext context) {
        this.context = context;
    }

    private DownloadStrategy next() {
        if (index >= list.size()) {
            return null;
        }
        return list.get(index++);
    }

    public void doChain() throws Exception {
        DownloadStrategy strategy = this.next();
        if (strategy != null) {
            strategy.execute(this);
        } else {
            index = 0;
        }
    }

    public void add(DownloadStrategy strategy) {
        this.list.add(strategy);
    }

    public void addAll(List<DownloadStrategy> list) {
        this.list.addAll(list);
    }

    public ApplicationContext getContext() {
        return context;
    }
}
