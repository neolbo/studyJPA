package jpa_ex.shop.domain;

import jakarta.persistence.*;
import jpa_ex.shop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
public class Category {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    @Setter
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<CategoryItem> categoryItems = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addChildCategory(Category child) {
        child.setParent(this);
        this.child.add(child);
    }

    public void addCategoryItem(CategoryItem categoryItem) {
        categoryItems.add(categoryItem);
        categoryItem.setCategory(this);
    }

    /**
     * ManyToMany == 중간 테이블이 정형화 되어있음 ==> 필드 추가 x
     * 따라서 실무에서 사용 x
     * 
     * 중간 테이블을 엔티티로 하나 만들어서 1:*, *:1 관계로 풀이
     */
/*
    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();
*/
}
