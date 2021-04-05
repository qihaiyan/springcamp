package cn.springcamp.springboot.unit.test.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MyDomainRepository extends JpaRepository<MyDomain, Long> {
}
