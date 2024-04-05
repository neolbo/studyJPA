package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    void 상품주문() {
        // given
        Member member = createMember();
        Item item = createItem("JPA", 10000, 10);
        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // then
        assertEquals(8, item.getStockQuantity(), "주문 수량만큼 재고가 줄어야한다.");
        Order findOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.ORDER, findOrder.getStatus(), "상품 주문 시 상태는 ORDER");
        assertEquals(1, findOrder.getOrderItems().size(), "주문한 상품 종류는 정확해야한다.");
        assertEquals(10000 * 2, findOrder.getTotalPrice(), "주문 가격은 가격*수량 이다.");
    }

    @Test
    void 상품주문_재고초과() {
        // given
        Member member = createMember();
        Item item = createItem("JPA", 10000, 10);
        int orderCount = 12;

        // when
        assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), item.getId(), orderCount));
    }

    @Test
    void 주문취소() {
        // given
        Member member = createMember();
        Item item = createItem("JPA", 10000, 10);
        int orderCount = 5;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        // when
        orderService.cancel(orderId);
        // then
        assertEquals(10, item.getStockQuantity(), "주문 취소 시 재고 복구");
        Order findOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL, findOrder.getStatus(), "주문 취소 시 상태는 CANCEL");
    }

    private Item createItem(String name, int price, int stockQuantity) {
        Item item = new Book();
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        em.persist(item);
        return item;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("Jeong");
        member.setAddress(new Address("대전", "강가", "123-123"));
        em.persist(member);
        return member;
    }

}