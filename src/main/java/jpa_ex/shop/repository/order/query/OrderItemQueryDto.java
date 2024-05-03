package jpa_ex.shop.repository.order.query;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemQueryDto {

    private Long orderId;
    private String name;
    private int price;
    private int count;
}
