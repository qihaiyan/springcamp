package cn.springcamp.springdynamicscheduler;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class MyScheduler implements SchedulingConfigurer {
    private ScheduledTaskRegistrar taskRegistrar;
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, CronTask> cronTasks = new ConcurrentHashMap<>();

    @Override
    public void configureTasks(@NonNull ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);// Set the pool of threads
        threadPoolTaskScheduler.setThreadNamePrefix("sys-scheduler");
        threadPoolTaskScheduler.initialize();
        this.taskRegistrar = taskRegistrar;
        this.taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }

    @PreDestroy
    public void destroy() {
        this.taskRegistrar.destroy();
    }

    public void registerTask(TaskData taskData) {
        //如果配置一致，则不需要重新创建定时任务
        if (scheduledFutures.containsKey(taskData.getId())
                && cronTasks.get(taskData.getId()).getExpression().equals(taskData.getExpression())) {
            return;
        }
        //如果策略执行时间发生了变化，则取消当前策略的任务
        if (scheduledFutures.containsKey(taskData.getId())) {
            scheduledFutures.remove(taskData.getId()).cancel(false);
            cronTasks.remove(taskData.getId());
        }

        CronTask task = new CronTask(taskData, taskData.getExpression());
        TaskScheduler scheduler = taskRegistrar.getScheduler();
        if (scheduler != null) {
            ScheduledFuture<?> future = scheduler.schedule(task.getRunnable(), task.getTrigger());
            if (future != null) {
                scheduledFutures.put(taskData.getId(), future);
            }
        }
    }

    public void stop(Long id) {
        if (scheduledFutures.containsKey(id)) {
            scheduledFutures.remove(id).cancel(false);
        }
    }
}
