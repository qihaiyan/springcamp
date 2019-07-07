package cn.springcamp.springdatajpa.multisource.primary.data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "demo")
public class PrimaryTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public PrimaryTable(){

    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id){
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
