package hellojpa.jpashop_Ex.domain;

import jakarta.persistence.*;

@Entity
@SequenceGenerator(name = "delivery_seq_generator", sequenceName = "delivery_seq")
public class Delivery {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "delivery_id")
    private Long id;

    private String city;
    private String street;
    private String zipcode;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @OneToOne(mappedBy = "delivery")
    private Order order;
}
