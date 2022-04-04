package cn.springcamp.springboot.datetimetoepoch.data;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
