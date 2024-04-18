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
            init(em);

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
//            conditional(em);

//            basic_function(em);

//            fetchJoin(em);

            namedQuery(em);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }

    private static void namedQuery(EntityManager em) {
        List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", "Member_A")
                .getResultList();

        for (Member member : resultList) {
            System.out.println("member = " + member);
        }

        List<Member> resultList1 = em.createNamedQuery("Member.findByAge", Member.class)
                .setParameter("age", 20)
                .getResultList();

        for (Member member : resultList1) {
            System.out.println("member = " + member);
        }
    }

    private static void init(EntityManager em) {
        Team team = new Team();
        team.setName("teamA");
        em.persist(team);

        Member member = new Member();
        member.setUsername("Member_A");
        member.setAge(10);
        member.changeTeam(team);
        em.persist(member);

        Member member2 = new Member();
        member2.setUsername("Member_B");
        member2.setAge(20);
        member2.changeTeam(team);
        em.persist(member2);

        em.flush();
        em.clear();
    }

    public static void fetchJoin(EntityManager em) {
        // 연관된 엔티티나 컬렉션을 한번에 조회하는 기능
        // LAZY fetch 되어 있더라도 join fetch 가 우선 순위  ==> EAGER 처럼 한번에 조회

        Team team = new Team();
        team.setName("teamB");
        em.persist(team);

        Member member1 = new Member();
        member1.setUsername("Member_C");
        member1.changeTeam(team);
        em.persist(member1);

        Member member2 = new Member();
        member2.setUsername("Member_D");
        em.persist(member2);        // 팀 없음

        em.flush();
        em.clear();
        // -------- setting

        /**
         * 일반 inner join 을 하면 lazy로 연관관계가 설정되어있기 때문에
         * 'select m from Member m' 쿼리에서의 team 은 프록시이다.
         * ==> member.getTeam() 으로 실제 team을 조회하려 할 때마다 쿼리를 새로 보내야한다.
         * -> n + 1 문제 발생
         */
/*
        String query = "select m from Member m join m.team t";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member + "team.name = " + member.getTeam().getName());
        }
*/
        // member_A => teamA(SQL)
        // member_B => teamA(1차 캐시)
        // member_C => teamBB(SQL)
        // 총 3번의 SQL 문이 나감.  모든 멤버의 팀이 다르다면 최대 n + 1번의 SQL 문이 필요함

/*
        */
/**
         * 새로운 DTO를 만들어 일반 inner join 하면 SQL 1개로 조회 가능하지만
         * mtdto.getMember().getUsername().... 접근하기가..
         *//*

        String query = "select new JPQL.MTDTO(m, t) from Member m join m.team t";
        List<MTDTO> resultList = em.createQuery(query, MTDTO.class).getResultList();
        for (MTDTO mtdto : resultList) {
            System.out.println("member = " + mtdto.getMember().getUsername() + "team.name = " + mtdto.getTeam().getName());
        }
*/

        /**
         * fetch join 을 사용하여 한번에 떙겨오기
         * member, team 한번에 select
         *
         * entity fetch join
         */
/*
        String query = "select m from Member m join fetch m.team";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
        for (Member member : resultList) {
            System.out.println("member = " + member + " team = " + member.getTeam().getName());
        }
*/
        /**
         * left join fetch
         */
/*
        String query = "select m from Member m left join fetch m.team";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
        for (Member member : resultList) {
            if(member.getTeam() == null) {
                System.out.println("member = " + member + " team = null");
            }
            else
                System.out.println("member = " + member + " team = " + member.getTeam().getName());
        }
*/

        /**
         * collection fetch join
         */
/*
        String query = "select t from Team t join fetch t.memberList";
        List<Team> resultList = em.createQuery(query, Team.class).getResultList();
        for (Team team1 : resultList) {
            System.out.println("team = " + team1 + "|" + "member = " + team1.getMemberList().size());
            for (Member member : team1.getMemberList()) {
                System.out.println("member = " + member);
            }
        }
*/

        /**
         * collection 을 fetch join 하면 paging 사용 불가
         *
         * 17:36 WARN  org.hibernate.orm.query - HHH90003004:
         *          firstResult/maxResults specified with collection fetch; applying in memory
         * ==> OneToMany 관계일 때 join 하면 데이터가 더 많아질 수 있기에 paging 하면 데이터 잘릴 수 있어 금지
         */
/*
        String query = "select t from Team t join fetch t.memberList";
        List<Team> resultList1 = em.createQuery(query, Team.class)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
        for (Team team1 : resultList1) {
            System.out.println("team1 = " + team1);
        }
*/

        /**
         *  해결 방안 1.
         *      OneToMany 를 뒤집어 ManyToOne 으로 select 해 paging 한다.
         *          'select m from Member m join fetch m.team' 으로 쿼리 날리고 paging
         *  해결 방안 2.
         *      @BatchSize(size = )이용  or  글로벌로 batchsize 설정 (<property name="hibernate.default_batch_fetch_size" value="100"/>)
         *          'select t from Team t' 를 날리고 paging  ==> t.getMember() 쓸 때마다 sql 나가지만
         *          @BatchSize() 이용하면 batchsize 만큼 team.id를 리스트로 들고가 member 한번에 조회
         *          batch size 는 1000 이하로 설정
         */
        String query = "select t from Team t";
        List<Team> resultList = em.createQuery(query, Team.class)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
        for (Team team1 : resultList) {
            System.out.println("team1 = " + team1);
            for (Member member : team1.getMemberList()) {
                System.out.println("member = " + member);
            }
        }

        /**
         * 아무튼 여러 테이블 조인해서 엔티티가 가진 모양이 아니고 비정제된 결과를 내야하면
         * fetch join 보단 일반 join 사용해서 DTO 로 변환!!
         */
    }

    public static void basic_function(EntityManager em) {
        // concat
//        String query = "select concat(m.username, m.age) from Member m";
//        String query = "select 'a' || 'b' from Member m";

        // substring
//        String query = "select substring(m.username, 1, 3) from Member m ";     // 첫번째부터 3개

        // trim         ==> 좌우 공백만 제거  => -관 리 자-
//        String query = "select trim(m.username) from Member m ";

        // lower, upper
//        String query = "select lower('ABCDE')";
        /*String query = "select upper('abcde')";

        List<String> resultList = em.createQuery(query, String.class).getResultList();
        for (String s : resultList) {
            System.out.println("s = " + s);
        }*/

        // length
//        String query = "select length('abcde')";

        // locate       // b 에 a 가 몇 번째에 있는지 반환 , 없으면 0
//        String query = "select locate('ll', 'hello')";
//        String query = "select locate('ab', 'djrabkdab', 5)";       // i 위치부터 b에 a가 처음으로 나오는 위치

//        String query = "select abs(-23)";
//        String query = "select sqrt(4)";        // double
//        String query = "select mod(4,3)";       // a를 b로 나눈 나머지

        // size     // collection size
        String query = "select size(t.memberList) from Team t";

        List<Integer> resultList = em.createQuery(query, Integer.class).getResultList();
        for (Integer i : resultList) {
            System.out.println("i = " + i);
        }



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
