package jpa_ex.shop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jpa_ex.shop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue
    private Long id;

//    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    @Setter
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    @Setter
    private Item item;

    @Setter
    private int price;
    @Setter
    private int count;

    // == 생성 메서드 == //
    public static OrderItem createOrderItem(Item item, int price, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setPrice(price);
        orderItem.setCount(count);
        item.removeStock(count);
        return orderItem;
    }

    // == 비지니스 로직 == //
    /**
     * 주문 취소
     */
    public void cancel() {
        getItem().addStock(count);
    }

    // == 조회 로직 == //

    /**
     * 주문 상품 가격 전체 조회
     */
    public int getTotalPrice() {
        return getPrice() * getCount();
    }
}
