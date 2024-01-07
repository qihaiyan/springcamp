package cn.springcamp.springdynamicscheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@EnableScheduling
@SpringBootApplication
@RestController
public class DemoApplication {
    @Autowired
    private MyScheduler myScheduler;
    @Autowired
    private TaskDataRepository taskDataRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @RequestMapping("/register")
    public TaskData register(
            String name,
            @RequestParam(name = "expression", required = false, defaultValue = "0/1 * * * * ?") String expression
    ) {
        TaskData taskData = taskDataRepository.findOneByName(name).orElse(new TaskData());
        taskData.setName(name);
        taskData.setExpression(expression);
        taskData = taskDataRepository.save(taskData);
        myScheduler.registerTask(taskData);
        return taskData;
    }

    @RequestMapping("/stop")
    public void stop(Long id) {
        taskDataRepository.findById(id).ifPresent(taskData -> {
            myScheduler.stop(id);
        });
    }
}
