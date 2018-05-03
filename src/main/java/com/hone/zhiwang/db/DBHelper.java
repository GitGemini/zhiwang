package com.hone.zhiwang.db;

import java.util.List;

public interface DBHelper {

    void init() throws Exception;

    void saveSuccess(String url, String title);

    boolean existOfSuccess(String title);

    void saveFail(String url, String title, int times);

    void deleteFail(int id);

    List<String> select() throws Exception;

    boolean existOfFail(String title);
}
