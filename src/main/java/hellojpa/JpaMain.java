package hellojpa;

import hellojpa.jpashop_Ex.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        //code

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setName("userA");
            em.persist(member);

            Member member2 = new Member();
            member2.setName("userB");
            em.persist(member2);

            em.flush();
            em.clear();

//            logic(member1, reference);

            //--------------

            /*Member refMember = em.getReference(Member.class, member.getId());
            Member findMember = em.find(Member.class, member.getId());

            System.out.println(findMember.getClass());
            System.out.println(refMember.getClass());

            System.out.println("a == a : " + (findMember == refMember));*/
            // JPA 에서 한 트랜잭션 안에서의 같은 엔티티 조회는 42번 줄을 만족해야함
            // 따라서 영속성 컨텍스트에 이미 찾는 엔티티가 있으면 해당 엔티티 반환

            //-------------

            /*Member findMember = em.getReference(Member.class, member.getId());
            System.out.println(findMember.getClass());

            em.detach(findMember);
            findMember.getName();*/
            // 초기화 하려는 프록시가 준영속성 상태이면 오류

            //-----------
            Member refMember = em.getReference(Member.class, member.getId());
            System.out.println("isInit?:" + emf.getPersistenceUnitUtil().isLoaded(refMember));
            System.out.println("getClass(): " + refMember.getClass());

            //강제 초기화        2가지 방법
//            refMember.getName();
            Hibernate.initialize(refMember);
            System.out.println("isInit?:" + emf.getPersistenceUnitUtil().isLoaded(refMember));

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }

    private static void logic(Member member1, Member reference) {

        /**
         * 프록시 객체도 결국 원본 entity 를 상속받아 만들어지기 때문에 파라미터로 받을 때 구별 x
         * 프록시가 들어오던 원본이 들어오던 상관 없게 == 이 아닌 instance of 사용
         */
        System.out.println("member1 == reference= " + (member1.getClass() == reference.getClass()));

        System.out.println("member1 == Member.class= " + (member1 instanceof Member));
        System.out.println("reference == Member.class= " + (reference instanceof Member));

    }
}
