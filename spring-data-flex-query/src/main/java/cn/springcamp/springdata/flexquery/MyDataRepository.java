package cn.springcamp.springdata.flexquery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MyDataRepository extends JpaRepository<MyData, Long>, JpaSpecificationExecutor<MyData> {
    @Query("select U from MyData U where (?1 is null or U.id=?1) and (?2 is null or U.name=?2)")
    List<MyData> findByQuery(Long id, String name);
}
