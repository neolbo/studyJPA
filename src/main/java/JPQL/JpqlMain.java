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
            // sub query

            Member member = new Member();
            member.setUsername("A");
            member.setAge(10);
            em.persist(member);


            em.flush();
            em.clear();

            // 나이가 평균 이상인 멤버
//            String query = "select m from Member as m where m.age > (select avg(m1.age) from Member as m1)";

            // 한 건이라도 주문한 고객
//            String query = "select m from Member as m where m = (select o.member from Order o)";
//            String query = "select m from Member as m where (select count(o) from Order o where m = o.member) > 0";

            // 지원함수

            // Exists  -- 서브쿼리에 결과가 존재하면 참
            // 팀 A 소속인 회원
//            String query = "select m from Member as m where exists (select t from m.team t where t.name = 'A')";

            // All -- 모두 만족하면 참
            // 전체 상품 각각의 재고보다 주문량이 많은 주문들
//            String query = "select o from Order as o where o.orderCount > All (select p.stockQuantity from Product as p)";

            // Any, Some  -- 조건을 하나라도 만족하면 참
            // 어떤 팀이든 소속된 회원
//            String query = "select m from Member as m where m.team = any (select t from Team t)";

            // in  -- 서브 쿼리의 결과 중 하나라도 같은 것이 있으면 참
            // 속한 팀이 a, b, c 팀 중에 있는 회원
            String query = "select m from Member as m where m.team = (select t from Team t where t.name in ('a', 'b', 'c') )";



            List<Member> resultList = em.createQuery(query, Member.class)
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
