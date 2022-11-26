package cn.springcamp.redisresolver;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class DemoEntity implements Serializable {
    @Id
    private Long id;
}
