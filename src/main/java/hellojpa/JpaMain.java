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
             * 객체 지향 모델링 시 양방향 매핑
             */

            // 저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setName("userA");

            member.changeTeam(team);

            // 연관관계 주인이 아닌 곳에 값을 입력해도 db에 반영 x
            // member.setTeam() 을 해줘야 반영 ==> 연관관계 주인인 member 쪽에 값 설정 해줘야함
            /**
             * team.getMembers().add(member);
             */
            // jpa 관점에서 보면 team.getMembers().add(member)는 없어도 상관 없지만
            // 객체 지향적 관점에서 보면 주인이 아닌 team 의 members 에도 추가하는게 좋음
            // ==>>>> 연관관계 편의 메서드를 사용
            
            // 양방향 매핑 사용 시 toString, lombok, json 등에서의 무한 루프 주의
            // toString 의 경우 member.toString 할 떄 team  , team.toString 에서의 members 무한 조회

            em.persist(member);

            Team findTeam = em.find(Team.class, member.getId());
            List<Member> members = findTeam.getMembers();
            for (Member m : members) {
                System.out.println("m.getName()=" + m.getName());
            }

            // 정확한 테스트 위해 영속성 1차 캐시 값 db에 올리고 삭제
            em.flush();
            em.clear();



            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
