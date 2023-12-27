package cn.springcamp.springdata.flexquery;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private MyDataRepository myDataRepository;

    @Override
    public void run(String... args) {
        MyData myData1 = new MyData();
        myData1.setName("one");
        myDataRepository.save(myData1);
        MyData myData2 = new MyData();
        myData2.setName("two");
        myDataRepository.save(myData2);

        log.info("find by id with query: {}", myDataRepository.findByQuery(1L, null));
        log.info("find by id and name with query: {}", myDataRepository.findByQuery(1L, "one"));

        MyData example = new MyData();
        example.setName("two");
        log.info("find by example: {}", myDataRepository.findAll(Example.of(example)));

        log.info("find by spec with id: {}", findBySpec(1L, null, null));
        log.info("find by spec with name: {}", findBySpec(null, "%wo%", null));
        log.info("find by spec with id list: {}", findBySpec(null, null, List.of(1L, 2L)));
    }

    private List<MyData> findBySpec(Long id, String name, List<Long> ids) {
        return myDataRepository.findAll((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (id != null) {
                predicates.add(builder.equal(root.get("id"), id));
            }
            if (name != null) {
                predicates.add(builder.like(root.get("name"), name));
            }
            if (ids != null) {
                predicates.add(root.get("id").in(ids));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        }, Sort.by("id").descending());
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
