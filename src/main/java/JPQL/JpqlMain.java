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
            // 프로젝션

            Member member = new Member();
            member.setUsername("A");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            // entity, embedded type (Address.class) 으로 프로젝션 가능

            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .getResultList();
            /**
             *  쿼리로 뽑아온 result(m) 도 영속성으로 관리 된다!
             */
            Member member1 = result.get(0);
            member1.setAge(20);
            // ==>> 이름이 A인 member 의 나이가 20으로 db 업데이트

            em.flush();
            em.clear();

            // distinct 로 중복 제거도 가능  select distinct ~~ from ~~

            /**
             *  스칼라 타입 프로젝션에서 값 가져오는 방법
             */
            // 방법 1. 타입 캐스팅해서 사용
        /*
            List resultList = em.createQuery("select m.username, m.age from Member as m")
                    .getResultList();
            Object o = resultList.get(0);
            Object[] r = (Object[]) o;
            System.out.println("name: " + r[0]);
            System.out.println("age: " + r[1]);
        */
            // 방법 2. Object[]로 query 반환
        /*
            List<Object[]> resultList = em.createQuery("select m.username, m.age from Member as m")
                    .getResultList();
            Object[] r = resultList.get(0);
            System.out.println("name: " + r[0]);
            System.out.println("age: " + r[1]);
        */
            // 방법 3. new 명령어로 조회 (DTO사용)
            // 쿼리문 작성을 string 으로 하기에 DTO 클래스명을 패키지까지 직접 작성해줘야함... import 불가
            // 찾고자하는 데이터와 (순서, 타입) 이 같은 '생성자' 필요
                    // 순서가 다르거나 타입이 다르면 IllegalStateException 발생
            List<MemberDTO> resultList = em.createQuery("select new JPQL.MemberDTO(m.username, m.age) from Member as m", MemberDTO.class)
                    .getResultList();
            MemberDTO memberDTO = resultList.get(0);
            System.out.println("memberDTO.getUsername(): " + memberDTO.getUsername());
            System.out.println("memberDTO.getAge(): " + memberDTO.getAge());

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
