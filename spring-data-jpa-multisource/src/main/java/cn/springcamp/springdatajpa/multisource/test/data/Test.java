package cn.springcamp.springdatajpa.multisource.test.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "test")
public class Test implements Serializable{

    @Id
    private Integer id;

    public Test(){

    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id){
        this.id = id;
    }
}
