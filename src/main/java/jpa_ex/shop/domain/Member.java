package jpa_ex.shop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private String name;

    @Embedded
    @Setter
    private Address address;

//    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Order> orderList = new ArrayList<>();
}
