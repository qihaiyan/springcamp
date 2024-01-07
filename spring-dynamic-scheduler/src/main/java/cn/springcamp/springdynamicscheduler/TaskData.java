package cn.springcamp.springdynamicscheduler;

import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Entity
public class TaskData implements Runnable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String expression;

    @Transient
    @Override
    public void run() {
        log.info("{} is running with expression {}", this.getName(), this.getExpression());
    }
}
