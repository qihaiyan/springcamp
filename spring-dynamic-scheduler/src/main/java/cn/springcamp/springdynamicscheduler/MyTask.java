package cn.springcamp.springdynamicscheduler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyTask implements Runnable {
    @Override
    public void run() {
        log.info("running");
    }
}
