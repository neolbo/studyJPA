package jpa_ex.shop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> resultList = findOrders();      // 컬렉션을 제외한 나머지 join fetch

        // 전제 resultList.orderItems 초기화
        resultList.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return resultList;

    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpa_ex.shop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.price, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpa_ex.shop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class
        ).getResultList();
    }

    /**
     * 최적화          query => 루트 1번, 컬레션 1번
     * 컬렉션을 in 으로 한번에 조회해서 메모리에 map 형식으로 저장 후 루프 돌면서 컬렉션 초기화
     * grouping by 메서드를 통해 MAP 형식으로 메모리에 저장
     */
    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> orders = findOrders();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(orders));

        orders.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return orders;
    }

    private static List<Long> toOrderIds(List<OrderQueryDto> orders) {
        return orders.stream().map(OrderQueryDto::getOrderId).toList();
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpa_ex.shop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.price, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class
                )
                .setParameter("orderIds", orderIds)
                .getResultList();

        return orderItems.stream().collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                        "select new jpa_ex.shop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.price, oi.count)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d" +
                                " join o.orderItems oi" +
                                " join oi.item i", OrderFlatDto.class
                )
                .getResultList();
    }
}
