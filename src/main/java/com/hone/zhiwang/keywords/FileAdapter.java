package com.hone.zhiwang.keywords;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter implements StoreAdapter {
    private static Logger logger = LoggerFactory.getLogger(FileAdapter.class);

    @Override
    public List<String[]> handler() {
        logger.info("解析关键字字典...");
        try {
            List<String[]> result = new ArrayList<String[]>();
            InputStream resourceAsStream = this.getClass().getResourceAsStream("/dc.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] array = line.split("\\+");
                result.add(array);
            }
            reader.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
