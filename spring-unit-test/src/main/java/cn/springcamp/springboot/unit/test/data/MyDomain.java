package cn.springcamp.springboot.unit.test.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class MyDomain {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private LocalDateTime createTime;
}
