package hellojpa.jpashop_Ex.domain;

import jakarta.persistence.*;

@Entity
@SequenceGenerator(name = "orderItem_seq_generator", sequenceName = "orderItem_seq")
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;
    private int orderPrice;
}
