package cn.springcamp.springnative;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface DemoDataRepository extends JpaRepository<DemoData, Long> {
    @Modifying
    @Query(value = "insert into demo_data(name) values (:#{[0].name})", nativeQuery = true)
    void insert0(DemoData demoData);

    @Modifying
    @Query(value = "insert into demo_data(name) values (:#{#demoData.name})", nativeQuery = true)
    void insert1(@Param("demoData") DemoData demoData);
}
