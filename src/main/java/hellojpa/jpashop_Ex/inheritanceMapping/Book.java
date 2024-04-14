package hellojpa.jpashop_Ex.inheritanceMapping;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("B")
public class Book extends Goods{
    private String author;
    private String isbn;
}
