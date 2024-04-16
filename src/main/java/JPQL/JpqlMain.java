package JPQL;

import jakarta.persistence.*;

import java.util.List;

public class JpqlMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPQL");
        EntityManager em = emf.createEntityManager();
        //code

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 페이징

            for(int i=0; i<100; ++i) {
                Member member = new Member();
                member.setUsername("Member" + i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            List<Member> resultList = em.createQuery("select m from Member as m order by m.age desc", Member.class)
                    .setFirstResult(5)      // 0번 index 부터 가능
                    .setMaxResults(10)
                    .getResultList();

            for (Member member : resultList) {
                System.out.println("member.toString() : " + member.toString());
            }

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
