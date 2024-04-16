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
            // 조인

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("Member");
            member.setAge(10);
            member.changeTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            // 내부 조인
//            String qlString = "select m from Member as m join m.team t";

            // 외부 조인
//            String qlString = "select m from Member m left join m.team t";

            // 세타 조인
//            String qlString = "select count(m) from Member m, Team t where m.username = t.name";

            // on 절 ( join 대상 필터링)
//            String qlString = "select m from Member m join m.team t on t.name = 'A'";

            // 연관 관계 없는 엔티티끼리의 외부 조인
            String qlString = "select m from Member m left join Team t on m.username = t.name";

            List<Member> resultList = em.createQuery(qlString, Member.class)
                    .getResultList();




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
