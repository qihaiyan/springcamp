package cn.springcamp.springdata.envers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface MyDataRepository extends JpaRepository<MyData, Long>, RevisionRepository<MyData, Long, Integer> {
}
