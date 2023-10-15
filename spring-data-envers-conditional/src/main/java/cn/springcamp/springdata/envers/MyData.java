package cn.springcamp.springdata.envers;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.envers.Audited;

@Data
@Entity
@Audited
public class MyData {
    @Id
    @GeneratedValue
    private Long id;

    private String author;
}
