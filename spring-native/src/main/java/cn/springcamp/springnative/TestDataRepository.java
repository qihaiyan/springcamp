package cn.springcamp.springnative;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestDataRepository extends JpaRepository<DemoData, Long> {
}
