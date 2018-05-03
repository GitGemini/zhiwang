package com.hone.zhiwang.keywords;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class KeywordStore {
    private static Logger logger = LoggerFactory.getLogger(KeywordStore.class);

    private int next = 0;

    private List<String[]> store = null;

    private StoreAdapter adapter = null;

    public KeywordStore(StoreAdapter adapter) {
        this.adapter = adapter;
    }

    public void init(){
        if (adapter == null) {
            return;
        }
        if (store == null) {
            store = adapter.handler();
        }
    }

    public boolean hasNext() {
        if (adapter == null) {
            return false;
        }
        if (store == null) {
            store = adapter.handler();
        }
        return next < store.size();
    }

    /**
     * 下一组编码的关键字
     *
     * @return
     */
    public String next() {
        if (!hasNext()) {
            return null;
        }
        String param = null;
        try {
            param = KeyEncoder.encode(store.get(next++));
        } catch (Exception e) {
            logger.info("请求参数处理失败！");
            e.printStackTrace();
        }
        return param;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getNext() {
        return next;
    }

    /**
     * 获取当前查询的关键字
     */
    public String getCurrentKeys() {
        return Arrays.toString(store.get(next - 1));
    }
    public String getNextKeys() {
        return Arrays.toString(store.get(next));
    }

    public void setAdapter(StoreAdapter adapter) {
        this.adapter = adapter;
    }

}
