package jpa_ex.shop.api;

import jpa_ex.shop.domain.Address;
import jpa_ex.shop.domain.Order;
import jpa_ex.shop.domain.status.OrderStatus;
import jpa_ex.shop.repository.order.OrderRepository;
import jpa_ex.shop.repository.order.OrderSearch;
import jpa_ex.shop.repository.order.simplequery.OrderSimpleQueryDto;
import jpa_ex.shop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


/**
 * xToOne ( ManyToOne, OneToOne 관계 최적화)
 * <p>
 * Order
 * Order -> Member
 * Order -> Delivery  조회
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * infinite recursion 발생  ==> order -> member -> orderList -> order -> ...
     * >> @JsonIgnore 사용
     * (무한 루프 없어짐 but LAZY 로딩에 의한 proxy member,delivery 이기에 예외 발생)
     * >> EAGER 는 사용 x ,  Hibernate5JakartaModule 사용 (Bean 등록)
     */
    @GetMapping("/api/v1/simple-orders")
    List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        // Hibernate5JakartaModule 설정 말고도 강제 LAZY 로딩 초기화 설정 가능
/*
        for (Order order : all) {
            order.getMember().getName();        // .getMember() 까지는 proxy,  .getName() 을 하면서 DB 접근
            order.getDelivery().getStatus();
        }
*/
        return all;
    }

    /**
     * Entity 를 조회해서 DTO 로 변환
     * Entity 를 직접 노출하지 않기 떄문에 @JsonIgnore 사용 안해도 됨
     * 
     * 그러나 query 가 n+1 번 날라가는 문제 발생  ==> fetch join 사용 해결
     */
    @GetMapping("/api/v2/simple-orders")
    Result<List<SimpleOrderDTO>> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDTO> orderDTOList = orders.stream()
                .map(SimpleOrderDTO::new)
                .toList();

        return new Result<>(orders.size(), orderDTOList);
    }

    /**
     * fetch join 사용
     *      query 1번
     */
    @GetMapping("/api/v3/simple-orders")
    Result<List<SimpleOrderDTO>> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDTO> list = orders.stream().map(SimpleOrderDTO::new).toList();
        return new Result<>(orders.size(), list);
    }

    /**
     * entity 조회 후 dto 로 변환하는 것이 아닌
     * JPA 에서 DTO 로 바로 조회!
     * - query 1번 && select 에서 원하는 데이터만 선택 조회
     */
    @GetMapping("/api/v4/simple-orders")
    Result<List<OrderSimpleQueryDto>> ordersV4() {
        List<OrderSimpleQueryDto> orderDtos = orderSimpleQueryRepository.findOrderDtos();
        return new Result<>(orderDtos.size(), orderDtos);
    }

    @Data
    static class SimpleOrderDTO {
        private Long orderId;
        private String name;
        private Address address;
        private LocalDateTime orderDate;
        private OrderStatus status;

        public SimpleOrderDTO(Order o) {
            orderId = o.getId();
            name = o.getMember().getName();
            address = o.getDelivery().getAddress();
            orderDate = o.getOrderDate();
            status = o.getStatus();
        }
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }
}
