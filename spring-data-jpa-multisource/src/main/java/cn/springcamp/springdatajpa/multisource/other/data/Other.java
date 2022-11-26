package cn.springcamp.springdatajpa.multisource.other.data;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "other")
public class Other implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
