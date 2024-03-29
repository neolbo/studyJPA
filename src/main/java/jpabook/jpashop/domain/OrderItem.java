package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "order_item")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;
    private int count;


    //==생성 메서드==//
    /**
     * @param orderPrice
     * item의 price 를 사용하지 않고 따로 받는 이유는
     * 할인 정책 등의 이유로 기존 상품 가격과 주문 가격이 다를 수 있기 때문
     */
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        item.removeStock(count);
        return orderItem;
    }

    //==비지니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        getItem().addStock(count);
    }

    //==조회 로직==//
    /**
     * 가격 조회
     */
    public int getTotalPrice() {
        return orderPrice * count;
        // getter 꼭 써야하나
    }
}
