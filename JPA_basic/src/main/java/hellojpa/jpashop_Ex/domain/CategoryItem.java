package hellojpa.jpashop_Ex.domain;

import jakarta.persistence.*;

@Entity
@SequenceGenerator(name = "category_item_seq_generator", sequenceName = "category_item_seq")
public class CategoryItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "category_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
