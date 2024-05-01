package jpa_ex.shop.repository.order.simplequery;

import jpa_ex.shop.domain.Address;
import jpa_ex.shop.domain.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private Address address;
    private LocalDateTime orderDate;
    private OrderStatus status;
}
