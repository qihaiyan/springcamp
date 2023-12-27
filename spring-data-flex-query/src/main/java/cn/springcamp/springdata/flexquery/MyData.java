package cn.springcamp.springdata.flexquery;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class MyData {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}
