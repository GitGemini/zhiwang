package com.hone.zhiwang.strategy;

import com.hone.zhiwang.keywords.FileAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 使用方差
 */
public class TimeLimitStrategy implements DownloadStrategy {
    private static Logger logger = LoggerFactory.getLogger(TimeLimitStrategy.class);

    /**
     * //平均下载间隔时间
     */
    private int averageTime = 15;

    /**
     * //前十个下载间隔时间
     */
    private List<Integer> before = new LinkedList<>();

    @Override
    public void execute(StrategyChain chain) throws Exception {
        long time = myFun();
        logger.info("随机策略：睡眠" + time + "s");
        Thread.sleep(time * 1000);
        chain.doChain();
    }

    private int myFun(){
        int number;
        if (before.size() < 10) {
            number = new Random().nextInt(averageTime + averageTime * 2 / 3) + averageTime / 3;
            before.add(number);
        } else {
            int sum = 0;
            for (Integer integer : before) {
                sum += (integer - averageTime) * (integer - averageTime) / (averageTime * 2 / 3);
            }
            before.remove(0);
            if (sum > 144) {
                number = averageTime - new Random().nextInt(averageTime / 2);
            } else if (sum > 36) {
                number = new Random().nextInt(new Double(averageTime * 2.5).intValue()) + averageTime - averageTime * 2 /3;
            } else {
                number = new Random().nextInt(averageTime * 3) + averageTime - averageTime * 2 /3;
            }
            before.add(number);
        }
        return number;
    }
}
