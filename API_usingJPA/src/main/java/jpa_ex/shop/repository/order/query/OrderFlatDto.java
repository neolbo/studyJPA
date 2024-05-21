package jpa_ex.shop.repository.order.query;

import jpa_ex.shop.domain.Address;
import jpa_ex.shop.domain.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderFlatDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Address address;

    private String itemName;
    private int price;
    private int count;
}
