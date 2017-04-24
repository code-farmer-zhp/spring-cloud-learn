package com.feiniu.run;


import com.feiniu.service.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class Run implements ApplicationListener<ContextRefreshedEvent> {

    private boolean isRun = false;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>());

    @Autowired
    private RunService runService;

    @Override
    public synchronized void onApplicationEvent(ContextRefreshedEvent event) {
        if (!isRun) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    runService.start();
                }
            });
            isRun = true;
        }
    }
}
