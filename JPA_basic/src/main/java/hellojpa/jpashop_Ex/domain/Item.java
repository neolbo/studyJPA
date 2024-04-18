package hellojpa.jpashop_Ex.domain;

import jakarta.persistence.*;

@Entity
@SequenceGenerator(name = "item_seq_generator", sequenceName = "item_seq")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "item_name")
    private String name;

    private int price;
    private int stockQuantity;
}
