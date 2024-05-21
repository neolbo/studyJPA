package jpa_ex.shop.service;

import jakarta.persistence.EntityManager;
import jpa_ex.shop.domain.Address;
import jpa_ex.shop.domain.Member;
import jpa_ex.shop.domain.Order;
import jpa_ex.shop.domain.item.Book;
import jpa_ex.shop.domain.item.Item;
import jpa_ex.shop.domain.status.DeliveryStatus;
import jpa_ex.shop.domain.status.OrderStatus;
import jpa_ex.shop.exception.NotEnoughStockException;
import jpa_ex.shop.repository.order.OrderRepository;
import jpa_ex.shop.repository.order.OrderSearch;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    Logger log = LoggerFactory.getLogger(getClass());


    @Test
    void 주문검색테스트() {
        // given
        Member member = createMember();
        Item item = createItem("A", 10000, 10);
        Long orderId = orderService.order(member.getId(), item.getId(), 5);

        em.flush();
        em.clear();
        // when
        OrderSearch orderSearch = new OrderSearch();
        List<Order> orders = orderService.findOrders(orderSearch);

        // then
        assertThat(orders.size()).isEqualTo(1);
        assertThat(orders.get(0).getMember().getName()).isEqualTo("userA");
        assertThat(orders.get(0).getOrderItems().get(0).getItem().getName()).isEqualTo("A");
    }

    @Test
    void 상품주문테스트() {
        // given
        Member member = createMember();

        Item item = createItem("bookA", 10000, 10);
        // when
        int count = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), count);

        // then
        Order findOrder = orderRepository.findOne(orderId);

        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(findOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(findOrder.getTotalPrice()).isEqualTo(10000 * count);
        assertThat(item.getStockQuantity()).isEqualTo(8);
    }

    @Test
    void 상품주문_재고수량초과() {
        // given
        Member member = createMember();
        Item item = createItem("A", 10000, 10);

        // when
        int count = 11;
        NotEnoughStockException e = assertThrows(NotEnoughStockException.class,
                () -> orderService.order(member.getId(), item.getId(), count));

        assertThat(e.getMessage()).isEqualTo("need more stock");
        // then
    }

    @Test
    void 주문취소() {
        // given
        Member member = createMember();
        Item item = createItem("A", 10000, 10);

        int count = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), count);

        assertThat(item.getStockQuantity()).isEqualTo(8);
        // when
        orderService.cancelOrder(orderId);
        // then
        Order findOrder = orderRepository.findOne(orderId);

        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(item.getStockQuantity()).isEqualTo(10);
    }

    @Test
    void 배송완료건은_취소불가능() {
        // given
        Member member = createMember();
        Item item = createItem("A", 10000, 10);
        int count = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), count);

        Order findOrder = orderRepository.findOne(orderId);
        findOrder.getDelivery().setStatus(DeliveryStatus.COMP);
        // when
        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
                orderService.cancelOrder(orderId));

        // then
        assertThat(e.getMessage()).isEqualTo("이미 배송 완료된 상품은 취소가 불가능합니다.");

        log.error(e.getMessage());
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
        member.setName("userA");
        member.setAddress(new Address("대전", "street", "123-123"));
        em.persist(member);
        return member;
    }
}