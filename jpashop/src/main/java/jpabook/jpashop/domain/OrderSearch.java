package jpabook.jpashop.domain;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class OrderSearch {

    private String memberName;      // 회원 이름
    private OrderStatus orderStatus;        // 주문 상태 order / cancel
}
