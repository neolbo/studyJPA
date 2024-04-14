package hellojpa;

import hellojpa.jpashop_Ex.domain.Member;
import hellojpa.jpashop_Ex.inheritanceMapping.Goods;
import hellojpa.jpashop_Ex.inheritanceMapping.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

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
            member.setCreatedBy("AA");
            member.setCreatedDate(LocalDateTime.now());
            member.setLastModifiedBy("BBB");
            member.setLastModifiedDate(LocalDateTime.now());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
