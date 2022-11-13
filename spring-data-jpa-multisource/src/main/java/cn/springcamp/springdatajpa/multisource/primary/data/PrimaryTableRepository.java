package cn.springcamp.springdatajpa.multisource.primary.data;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface PrimaryTableRepository extends JpaRepository<PrimaryTable, Long> {
    @Cacheable(value = "testCache", unless="#result == null")
    @NonNull
    Optional<PrimaryTable> findById(@NonNull Long id);
}