package cn.springcamp.springboot.datetimetoepoch.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MyDomainRepository extends JpaRepository<MyDomain, Long> {
}
