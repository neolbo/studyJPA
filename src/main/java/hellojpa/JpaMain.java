package hellojpa;

import jakarta.persistence.*;

import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        //code

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            /**
             * 데이터 등록
             */
            /*Member member = new Member();
            member.setId(3L);
            member.setName("HelloDDDD");

            em.persist(member);*/

            /**
             * 데이터 수정
             */
            /*Member findMember = em.find(Member.class, 1L);
            findMember.setName("HelloJPA");*/

            /**
             * 데이터 삭제
              */
            /*Member removeMember = em.find(Member.class, 2L);
            em.remove(removeMember);*/

            /**
             * 특정 데이터 조회
             */
            List<Member> resultList = em.createQuery("select m from Member m where m.id >= 2L ", Member.class)
                    .getResultList();
            for (Member member : resultList) {
                System.out.println("member.name = " + member.getName());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
