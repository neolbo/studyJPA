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
             * 객체를 테이블에 맞추어 모델링 시
             */

            // 저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setName("userA");
            member.setTeam_id(team.getId());
            em.persist(member);

            // 조회 시  - 다른 트렌젝션에 있다고 가정
            Member findMember = em.find(Member.class, 1L);
            Team findTeam = em.find(Team.class, findMember.getTeam_id());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
