package jpa_ex.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jpa_ex.shop.web.BookForm;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue("B")
public class Book extends Item {
    private String author;
    private String isbn;
}
