package hellojpa.jpashop_Ex.inheritanceMapping;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("A")
public class Album extends Goods {
    private String artist;
}
