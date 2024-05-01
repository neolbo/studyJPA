package jpa_ex.shop.repository.order;

import jpa_ex.shop.domain.status.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearch {
    private String memberName;
    private OrderStatus orderStatus;
}
