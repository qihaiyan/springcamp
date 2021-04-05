package cn.springcamp.springboot.unit.test.data;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class MyDomain {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}
