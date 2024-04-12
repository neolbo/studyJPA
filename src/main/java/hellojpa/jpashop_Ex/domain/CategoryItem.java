package hellojpa.jpashop_Ex.domain;

import jakarta.persistence.*;

@Entity
@SequenceGenerator(name = "category_item_seq_generator", sequenceName = "category_item_seq")
public class CategoryItem {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "category_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
