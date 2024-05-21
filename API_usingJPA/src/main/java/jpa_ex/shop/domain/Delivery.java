package jpa_ex.shop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jpa_ex.shop.domain.status.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
public class Delivery {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    @Setter
    private Address address;

    @Enumerated(EnumType.STRING)
    @Setter
    private DeliveryStatus status;

    @JsonIgnore
    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    @Setter
    private Order order;
}
