package jpa_ex.shop.api;

import jpa_ex.shop.domain.Address;
import jpa_ex.shop.domain.Order;
import jpa_ex.shop.domain.OrderItem;
import jpa_ex.shop.domain.status.OrderStatus;
import jpa_ex.shop.repository.order.OrderRepository;
import jpa_ex.shop.repository.order.OrderSearch;
import jpa_ex.shop.repository.order.query.OrderFlatDto;
import jpa_ex.shop.repository.order.query.OrderItemQueryDto;
import jpa_ex.shop.repository.order.query.OrderQueryDto;
import jpa_ex.shop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * 1.
     * entity 직접 노출
     * =>> @JsonIgnore (양방향 관계 문제 => infinite recursion 방지)
     * Hibernate5JakartaModule 이용, LAZY 강제 초기화
     */
    @GetMapping("/api/v1/orders")
    ApiResult<List<Order>> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        // LAZY 강제 초기화
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.forEach(o -> o.getItem().getName());
        }
        return new ApiResult<>(all.size(), all);
    }

    /**
     * 2.
     * Dto 변환 (fetch join 사용 x 버전)
     * ==> LAZY 강제 초기화 필요
     */
    @GetMapping("/api/v2/orders")
    ApiResult<List<OrderDto>> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> orderDtoList = orders.stream().map(OrderDto::new).toList();

        return new ApiResult<>(orderDtoList.size(), orderDtoList);
    }

    /**
     * 3.
     * Dto 변환 (fetch join 사용)
     * ==> 컬렉션 페치 조인 사용 시 페이징 불가 , 컬렉션 페치 조인은 1개만 사용 가능
     */
    @GetMapping("/api/v3/orders")
    ApiResult<List<OrderDto>> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        for (Order order : orders) {
            System.out.println("order ref = " + order + "id = " + order.getId());
        }

        List<OrderDto> orderDtoList = orders.stream().map(OrderDto::new).toList();
        return new ApiResult<>(orderDtoList.size(), orderDtoList);
    }

    /**
     * 4.
     * collection fetch join 의 paging
     * ==> paging query 에 영향을 주지 않는 _ToOne 관계는 모두 fetch join 적용 후
     * 컬렉션은 지연 로딩으로 조회
     * hibernate.default_batch_fetch_size  /  @BatchSize 적용으로 지연로딩 최적화
     */
    @GetMapping("/api/v3.1/orders")
    ApiResult<List<OrderDto>> ordersV3_1() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();       // ToOne 관계 fetch join
        List<OrderDto> orderDtoList = orders.stream().map(OrderDto::new).toList();      // collection LAZY loading
        return new ApiResult<>(orderDtoList.size(), orderDtoList);

        /**
         * batch size 적용으로 인해
         * 1 + n + n  ==> 1 + 1 + 1 로 줄어듦
         */
    }

    /**
     * paging
     */
    @GetMapping("/api/v3.2/orders")
    ApiResult<List<OrderDto>> ordersV3_2(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);      // paging 된 orders
        List<OrderDto> orderDtoList = orders.stream().map(OrderDto::new).toList();
        return new ApiResult<>(orderDtoList.size(), orderDtoList);
    }

    // collection join fetch /와/ ToOne join fetch 후 collection LAZY loading 차이
    /**
     * collection join fetch 를 사용하면 query 는 하나만 나가지만 db 에서 정제된 데이터를 뿌리는게 아니기에
     * 수 많은 데이터가 db -> application 으로 넘어온다.
     *
     * 하지만 LAZY 로딩 시에는 only join fetch 보다는 query 수가 조금 늘지만 데이터 정제되어 들고와서 좋다.
     *
     * 암튼 네트워크 트래픽과 db -> application 데이터 잘 고려해서 사용
     */

    /**
     * JPA 에서 DTO 직접 조회
     */
    @GetMapping("/api/v4/orders")
    ApiResult<List<OrderQueryDto>> ordersV4() {
        List<OrderQueryDto> orderQueryDtos = orderQueryRepository.findOrderQueryDtos();
        return new ApiResult<>(orderQueryDtos.size(), orderQueryDtos);
    }

    /**
     * DTO 직접 조회 _ 컬렉션 최적화
     */
    @GetMapping("/api/v5/orders")
    ApiResult<List<OrderQueryDto>> ordersV5() {
        List<OrderQueryDto> all = orderQueryRepository.findAllByDto_optimization();
        return new ApiResult<>(all.size(), all);
    }

    /**
     * DTO 직접 조회 _ 플랫 데이터 최적화
     */
    @GetMapping("/api/v6/orders")
    ApiResult<List<OrderQueryDto>> ordersV6() {
        List<OrderFlatDto> all = orderQueryRepository.findAllByDto_flat();
        // ToMany 를 join 했기 떄문에 OrderFlatDto 로 반환 시 데이터 뻥튀기
        // OrderQueryDto 로 API 스펙 맞추려면 중복 제거 필요

        List<OrderQueryDto> result = all.stream()
                .collect(groupingBy(o ->
                                new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(),
                        e.getKey().getOrderDate(), e.getKey().getStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());

        return new ApiResult<>(result.size(), result);
    }


    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private Address address;
        private LocalDateTime orderDate;
        private OrderStatus status;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order o) {
            orderId = o.getId();
            name = o.getMember().getName();
            address = o.getDelivery().getAddress();
            orderDate = o.getOrderDate();
            status = o.getStatus();
            orderItems = o.getOrderItems().stream().map(OrderItemDto::new).toList();
        }
    }

    @Data
    static class OrderItemDto {

        private String name;
        private int price;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            name = orderItem.getItem().getName();
            price = orderItem.getPrice();
            count = orderItem.getCount();
        }
    }
}
