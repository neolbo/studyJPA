package jpa_ex.shop.domain;

import jakarta.persistence.*;
import jpa_ex.shop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
public class CategoryItem {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    @Setter
    private Category category;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 필드 추가 가능..

}
