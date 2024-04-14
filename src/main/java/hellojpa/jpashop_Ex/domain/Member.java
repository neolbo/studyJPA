package hellojpa.jpashop_Ex.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(name = "member_seq_generator", sequenceName = "member_seq")
public class Member extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "user_name")
    private String name;
    private String city;
    private String street;
    private String zipcode;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
