package JPQL;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class JpqlMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPQL");
        EntityManager em = emf.createEntityManager();
        //code

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("MemberA");
            member.setAge(10);
            member.changeTeam(team);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("관리자");
            member2.setAge(10);
            member2.changeTeam(team);
            em.persist(member2);

            em.flush();
            em.clear();

            // projection
//            projection(em);

            // paging
//            paging(em);

            // join
//            join(em);

            // sub query
//          subquery(em);

            // type
//            type(em);

            // conditional
            conditional(em);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }

    public static void conditional(EntityManager em) {
        // 기본 case 식
/*        String query = "select " +
                "case when m.age<=10 then '학생 요금' " +
                "     when m.age>=60 then '경로 요금' " +
                "     else '일반 요금' " +
                "end " +
                "from Member m";

        List<String> resultList = em.createQuery(query, String.class).getResultList();
        for (String s : resultList) {
            System.out.println("s = " + s);
        }
*/
        // coalesce => 하나씩 조회해서 null 이 아니면 반환
/*        String query = "select coalesce(m.username, '이름이 없는 회원') from Member m";
        List<String> resultList = em.createQuery(query, String.class).getResultList();
        for (String s : resultList) {
            System.out.println("s = " + s);
        }
*/
        // nullif => 두 값이 같으면 null 반환,  다르면 첫번째 값 반환
        // 사용자 이름이 '관리자' 이면 null 반환     => 관리자 숨길 때 사용
        String query = "select nullif(m.username, '관리자') from Member m";
        List<String> resultList = em.createQuery(query, String.class).getResultList();
        for (String s : resultList) {
            System.out.println("s = " + s);
        }
    }

    public static void type(EntityManager em) {
        // 문자는 ' ' 로 ,  she's 같은 ' 는 'she''s' 로 표현  ' => ''
//        String query = "select m from Member m where m.username = 'A'";

        // 숫자는 10L(Long) 20F(Float) 30D(Double)
//        String query = "select m from Member m where m.age = 10L";
//
//        List<Member> resultList = em.createQuery(query, Member.class)
//                .getResultList();

        // boolean => true, false
        /*List<Object[]> resultList = em.createQuery("select m, true from Member m").getResultList();
        Object[] o = resultList.get(0);
        System.out.println(o[0]);       // m
        System.out.println(o[1]);       // true*/

        // enum
//        String query = "select m from Member m where m.memberType = JPQL.MemberType.ADMIN";     // 하드코딩 시 패키지명
//        String query = "select m from Member m where m.memberType = :memberType";
//        List<Member> resultList = em.createQuery(query, Member.class)
//                .setParameter("memberType", MemberType.USER)
//                .getResultList();

        // type(m)  ==> 상속 관계에서 사용
//        em.createQuery("select i from Item i where type(i) = Book", Item.class)
//                .getResultList();

    }

    public static void projection(EntityManager em) {
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
    }

    public static void paging(EntityManager em) {
        for (int i = 1; i < 100; ++i) {
            Member member = new Member();
            member.setUsername("Member" + i);
            member.setAge(i);
            em.persist(member);
        }

        List<Member> resultList = em.createQuery("select m from Member as m order by m.age desc", Member.class)
                .setFirstResult(5)      // 0번 index 부터 가능
                .setMaxResults(10)
                .getResultList();

        for (Member member : resultList) {
            System.out.println("member.toString() : " + member.toString());
        }
    }

    public static void join(EntityManager em) {
        // 내부 조인
//            String qlString = "select m from Member as m join m.team t";

        // 외부 조인
//            String qlString = "select m from Member m left join m.team t";

        // 세타 조인
//            String qlString = "select count(m) from Member m, Team t where m.username = t.name";

        // on 절 ( join 대상 필터링)
//            String qlString = "select m from Member m join m.team t on t.name = 'A'";

        // 연관 관계 없는 엔티티끼리의 외부 조인
        String query = "select m from Member m left join Team t on m.username = t.name";

        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
    }

    public static void subquery(EntityManager em) {
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

        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
    }
}
