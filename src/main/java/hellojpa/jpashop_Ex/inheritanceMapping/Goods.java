package hellojpa.jpashop_Ex.inheritanceMapping;

import jakarta.persistence.*;


/**
 * 상속 매핑의 기본 전략 => single table
 *
 * 전략 변경 시 @inheritance 사용
 * (JOINED, SINGLE_TABLE, TABLE_PER_CLASS)  table per class 전략은 사용 x
 *
 * joined 전략 시 DTYPE 은 실행하는데에 필요 없어 자동 생성 x
 * 생성 시 @DiscriminatorColumn 사용 / DTYPE 기본 값 = 상속하는 entity 명  변경 시 @DiscriminatorValue 사용
 * single table 전략에서는 DTYPE 이 필수이기에 @DiscriminatorColumn 생략 가능
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
public abstract class Goods {

    @Id @GeneratedValue
    private Long id;
    private String name;
    private int price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
