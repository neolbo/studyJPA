package jpa_ex.shop.domain.item;

import jakarta.persistence.*;
import jpa_ex.shop.domain.Category;
import jpa_ex.shop.domain.CategoryItem;
import jpa_ex.shop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Item_Type")
public abstract class Item {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;

    @OneToMany(mappedBy = "item")
    private List<CategoryItem> categoryItems = new ArrayList<>();

    // ===== 비지니스 로직 ====
    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int rest = stockQuantity - quantity;
        if (rest < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        stockQuantity = rest;
    }
/*
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
*/
}
