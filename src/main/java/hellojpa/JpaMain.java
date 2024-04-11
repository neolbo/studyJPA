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
             * 객체 지향 모델링 시
             */

            // 저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Team newTeam = new Team();
            newTeam.setName("TeamB");
            em.persist(newTeam);

            Member member = new Member();
            member.setName("userA");
            member.setTeam(team);
            em.persist(member);

            // 조회 시  - 다른 트렌젝션에 있다고 가정
            Member findMember = em.find(Member.class, 1L);
            Team findTeam = findMember.getTeam();

            System.out.println("findTeam.getName()=" + findTeam.getName());

            // 멤버의 팀 수정
            member.setTeam(newTeam);
            Member findNewMember = em.find(Member.class, 1L);
            Team findNewTeam = findMember.getTeam();
            System.out.println("findNewTeam.getName()=" + findNewTeam.getName());


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
