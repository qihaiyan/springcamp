package cn.springcamp.springdynamicscheduler;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskDataRepository extends JpaRepository<TaskData, Long> {
    Optional<TaskData> findOneByName(String name);
}
