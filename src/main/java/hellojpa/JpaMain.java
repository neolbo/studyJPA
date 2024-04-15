package hellojpa;

import hellojpa.jpashop_Ex.domain.Address;
import hellojpa.jpashop_Ex.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Set;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        //code

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 값 타입 컬렉션 사용

            // 컬렉션 저장
            Member member = new Member();
            member.setName("AA");
            member.setHomeAddress(new Address("HomeCity", "street", "10000"));
            
            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("피자");
            member.getFavoriteFoods().add("햄버거");

            member.getAddressHistory().add(new Address("Old1", "street", "123"));
            member.getAddressHistory().add(new Address("Old2", "street", "456"));

            em.persist(member);

            em.flush();
            em.clear();

            // ---------- 컬렉션 조회  -------------
            System.out.println("======= 컬렉션 조회  =========");
            Member findMember = em.find(Member.class, member.getId());
            // 값 타입 컬렉션 쿼리 x ==> 컬렉션은 지연 로딩 전략

            Set<String> favoriteFoods = findMember.getFavoriteFoods();
            for (String favoriteFood : favoriteFoods) {
                System.out.println(favoriteFood);
            }

            List<Address> addressHistory = findMember.getAddressHistory();
            for (Address address : addressHistory) {
                System.out.println(address.getCity());
            }

            em.clear();

            // ---------- 컬렉션 수정  -------------
            System.out.println("======= 컬렉션 수정  =========");
            Member findMember1 = em.find(Member.class, member.getId());

            // HomeCity ==> NewCity 변경
            System.out.println("======= HomeCity ==> NewCity 변경  =========");
            Address homeAddress = findMember1.getHomeAddress();
            findMember1.setHomeAddress(new Address("NewCity", homeAddress.getStreet(), homeAddress.getZipcode()));

            em.flush();

            // favorite_food 치킨 -> 족발 변경
            System.out.println("======= favorite_food 치킨 -> 족발 변경  =========");
            findMember1.getFavoriteFoods().remove("치킨");
            findMember1.getFavoriteFoods().add("족발");

            em.flush();

            // address_history Old1 -> homeCity 수정
            System.out.println("======= address_history Old1 -> homeCity 변경  =========");
            findMember1.getAddressHistory().remove(new Address("Old1", "street", "123"));
            // remove(Object) => equals 로 비교해서 같은 값 삭제 ==>>>> address 에서의 equals 재정의 필수
            findMember1.getAddressHistory().add(new Address("homeCity", "street", "123"));
            /**
             *  address history 에서 하나를 삭제하고 하나를 추가해 수정하는 것을 의도하였지만
             * 값 타입은 엔티티와 다르게 식별자 개념이 없어 값이 변경되면 추적하기 어렵다.
             *  (address 라는 값 타입 컬렉션이 변경되어 테이블에도 적용해야하는데 어느 address가 변경되었는지
             *  추적 불가능인듯)
             *  따라서 table 값 전체를 지우고 컬렉션의 모든 값을 다시 insert 한다. ==> 사용하기 어렵다!
             *
             *  그렇기에 엔티티 만들고
             *  일대다 (cascade, orphanRemoval 을 사용) 매핑하여 값 타입 컬렉션처럼 사용!!
             */

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}
