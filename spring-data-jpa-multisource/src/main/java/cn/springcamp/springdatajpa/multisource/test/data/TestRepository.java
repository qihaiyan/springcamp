package cn.springcamp.springdatajpa.multisource.test.data;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Integer> {
    @Cacheable(value = "testCache")
    public Test findOne(Integer id);
}