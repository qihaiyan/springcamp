package cn.springcamp.redisresolver;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity
public class DemoEntity implements Serializable {
    @Id
    private Long id;
}
