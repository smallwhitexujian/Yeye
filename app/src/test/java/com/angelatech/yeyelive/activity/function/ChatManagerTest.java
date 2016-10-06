package com.angelatech.yeyelive.activity.function;

import com.angelatech.yeyelive.util.PausableThreadPoolExecutor;
import com.angelatech.yeyelive.util.PriorityRun;

import org.junit.Test;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * 　　┏┓　　　　┏┓
 * 　┏┛┻━━━━┛┻┓
 * 　┃　　　　　　　　┃
 * 　┃　　　━　　　　┃
 * 　┃　┳┛　┗┳　　┃
 * 　┃　　　　　　　　┃
 * 　┃　　　┻　　　　┃
 * 　┃　　　　　　　　┃
 * 　┗━━┓　　　┏━┛
 * 　　　　┃　　　┃　　　神兽保佑
 * 　　　　┃　　　┃　　　代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 *
 *
 * 作者: Created by: xujian on Date: 16/8/10.
 * 邮箱: xj626361950@163.com
 * com.angelatech.yeyelive.activity.function
 * 线程池控制
 */
public class ChatManagerTest {
    @Test
    public void addChatMessage() throws Exception {
        PausableThreadPoolExecutor pausableThreadPoolExecutor = new PausableThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>());
        for (int i = 1; i <= 100; i++) {
            final int priority = i;
            final int finalI = i;
            pausableThreadPoolExecutor.execute(new PriorityRun(priority) {
                @Override
                public void doSth() {
                    System.out.println("----执行次数---->"+ finalI);
                }
            });
        }
    }

}