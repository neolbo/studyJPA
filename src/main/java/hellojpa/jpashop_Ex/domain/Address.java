package hellojpa.jpashop_Ex.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;


/**
 * embedded type 은 기본 생성자 필수
 * 
 * 값 타입은 무조건 불변으로 만들어야 side-effect 방지  ==> setter 삭제
 *
 * 후에 값 변경 원할 시  새로이 만들어 member.setAddress 로 바꿔치기 해야함
 */
@Embeddable
public class Address {
    @Column(length = 10)
    private String city;
    @Column(length = 20)
    private String street;
    @Column(length = 5)
    private String zipcode;

    public Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    
    // 객체 지향적으로 메서드 분리해서 생성 가능
    public String fullAddress() {
        return getCity() + " " + getStreet() + " " + getZipcode();
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getZipcode() {
        return zipcode;
    }


    /**
     *  값 타입 비교 시 equals 를 사용하여 동등성 비교 해야함
     *
     *  각 타입에 맞게 equals 재정의 후 사용
     *  default 가 == 비교이므로
     *
     *  getter 로 접근하게 생성
     *  직접 접근하게 생성하면 proxy 에서는 값 접근 불가
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(getCity(), address.getCity()) && Objects.equals(getStreet(), address.getStreet()) && Objects.equals(getZipcode(), address.getZipcode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCity(), getStreet(), getZipcode());
    }
}
