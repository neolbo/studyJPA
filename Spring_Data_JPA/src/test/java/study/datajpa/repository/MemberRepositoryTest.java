package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.*;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    EntityManager em;

    @Test
    void memberRepositoryTest () {
        // given
        Member member = new Member("memberA", 10);
        Member savedmember = memberRepository.save(member);
        // when
        Member findMember = memberRepository.findById(member.getId()).orElseGet(() -> null);    // 없으면 그냥 null 반환
        // then
        assertThat(findMember).isEqualTo(member);
        assert findMember != null;
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("userA", 10);
        Member member2 = new Member("userB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    void queryMethodTest() {
        // given
        Member member1 = new Member("userA", 10);
        Member member2 = new Member("userB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        // when
        List<Member> findMembers = memberRepository.findByUsernameAndAgeGreaterThan("userA", 1);
        // then
        assertThat(findMembers.get(0)).isEqualTo(member1);
    }

    @Test
    void findUser() {
        // given
        Member member1 = new Member("userA", 10);
        Member member2 = new Member("userB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        // when
        List<Member> findMembers = memberRepository.findUser("userA", 10);
        // then
        assertThat(findMembers.get(0)).isEqualTo(member1);
    }

    @Test
    void findUsernameList() {
        // given
        Member member1 = new Member("userA", 10);
        Member member2 = new Member("userB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        // when
        List<Member> all = memberRepository.findAll();
        List<String> usernameList = memberRepository.findUsernameList();
        // then
        int i =0;
        for (String s : usernameList) {
            assertThat(s).isEqualTo(all.get(i).getUsername());
            i++;
        }

        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }
    
    @Test
    void findMemberDto() {
        // given
        Team team = new Team("TeamA");
        teamRepository.save(team);

        Member member = new Member("userA", 10);
        member.changeTeam(team);
        memberRepository.save(member);
        // when
        List<MemberDto> memberDto = memberRepository.findMemberDto();
        // then
        assertThat(memberDto.get(0).getId()).isEqualTo(member.getId());
        assertThat(memberDto.get(0).getUsername()).isEqualTo(member.getUsername());
        assertThat(memberDto.get(0).getTeamName()).isEqualTo(team.getTeamName());
    }

    @Test
    void findByNames() {
        // given
        Member member1 = new Member("userA", 10);
        Member member2 = new Member("userB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        // when
        List<Member> list = Arrays.asList(member1, member2);
        List<Member> members = memberRepository.findByNames(Arrays.asList("userA", "userB"));
        // then
        int i=0;
        for (Member member : members) {
            assertThat(member).isEqualTo(list.get(i));
            i++;
        }

        for (Member member : members) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void returnType() {
        // given
        Member member1 = new Member("userA", 10);
        Member member2 = new Member("userB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        // when
        List<Member> userA = memberRepository.findListByUsername("userC");
        // collection 반환 시 위처럼 값이 없으면 null 반환이 아닌 빈 Collection 반환

        Member userA1 = memberRepository.findMemberByUsername("userC");
        Optional<Member> userA2 = memberRepository.findOptionalByUsername("userA");
        // 단건 조회 시 값이 더 많으면 NonUniqueResultException 반환
        // 없으면 null 반환
        // then
    }

    @Test
    void PagingTest() {
        // given
        memberRepository.save(new Member("user1", 10));
        memberRepository.save(new Member("user2", 10));
        memberRepository.save(new Member("user3", 10));
        memberRepository.save(new Member("user4", 10));
        memberRepository.save(new Member("user5", 10));
        memberRepository.save(new Member("user6", 20));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "username"));
        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        // then
        List<Member> content = page.getContent();   // 조회된 데이터

        for (Member member : content) {
            System.out.println("member.getUsername() = " + member.getUsername());
        }


        assertThat(content.size()).isEqualTo(2);        // 조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5);   // 전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(1);      // 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2);      // 전체 페이지 번호
        assertThat(page.isFirst()).isFalse();        // 첫번째 페이지 인가
        assertThat(page.hasNext()).isFalse();        // 다음 페이지가 있는가
    }

    @Test
    void SliceTest() {
        // given
        memberRepository.save(new Member("user1", 10));
        memberRepository.save(new Member("user2", 10));
        memberRepository.save(new Member("user3", 10));
        memberRepository.save(new Member("user4", 10));
        memberRepository.save(new Member("user5", 10));
        memberRepository.save(new Member("user6", 20));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "username"));
        // when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);
        // then
        List<Member> content = page.getContent();   // 조회된 데이터

        for (Member member : content) {
            System.out.println("member.getUsername() = " + member.getUsername());
        }


        assertThat(content.size()).isEqualTo(2);        // 조회된 데이터 수
        assertThat(page.getNumber()).isEqualTo(1);      // 페이지 번호
        assertThat(page.getSize()).isEqualTo(3);      // 페이지 최대 데이터 수
        assertThat(page.getNumberOfElements()).isEqualTo(2);        // 현재 페이지의 데이터 수
        assertThat(page.hasContent()).isTrue();     // 조회된 데이터 존재 여부
        System.out.println("page.getSort() = " + page.getSort());       // sort 정보
        assertThat(page.isFirst()).isFalse();        // 첫번째 페이지 인가
        assertThat(page.hasNext()).isFalse();        // 다음 페이지가 있는가
        assertThat(page.isLast()).isTrue();     // 마지막 페이지 인지 여부
        assertThat(page.hasPrevious()).isTrue();        // 이전 페이지 유무

    }

    @Test
    void topTest() {
        // given
        memberRepository.save(new Member("user1", 10));
        memberRepository.save(new Member("user2", 10));
        memberRepository.save(new Member("user3", 10));
        memberRepository.save(new Member("user4", 10));
        memberRepository.save(new Member("user5", 10));
        memberRepository.save(new Member("user6", 20));

        int age = 10;
        // when
        List<Member> list = memberRepository.findTop3ByAge(age);
        // then
        for (Member member : list) {
            System.out.println("member.getUsername() = " + member.getUsername());
        }
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    void FirstTest() {
        // given
        memberRepository.save(new Member("user1", 10));
        memberRepository.save(new Member("user2", 10));
        memberRepository.save(new Member("user3", 10));
        memberRepository.save(new Member("user4", 10));
        memberRepository.save(new Member("user5", 10));
        memberRepository.save(new Member("user6", 20));

        int age = 10;
        // when
        List<Member> list = memberRepository.findFirst4ByAge(age);
        // then
        for (Member member : list) {
            System.out.println("member.getUsername() = " + member.getUsername());
        }
        assertThat(list.size()).isEqualTo(4);
    }
    
    @Test
    void pageDtoConversion() {
        // given
        memberRepository.save(new Member("user1", 10));
        memberRepository.save(new Member("user2", 10));
        memberRepository.save(new Member("user3", 10));
        memberRepository.save(new Member("user4", 10));
        memberRepository.save(new Member("user5", 10));
        memberRepository.save(new Member("user6", 20));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "username"));
        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);
        // when
        Slice<MemberDto> toDto = slice.map(s -> new MemberDto(s.getId(), s.getUsername(), null));
        // 페이지로도 가능

        // then
        assertThat(toDto.getContent().size()).isEqualTo(2);
    }

    @Test
    @Rollback(value = false)
    void bulkTest() {
        // given
        memberRepository.save(new Member("user1", 10));
        memberRepository.save(new Member("user2", 19));
        memberRepository.save(new Member("user3", 20));
        memberRepository.save(new Member("user4", 21));
        memberRepository.save(new Member("user5", 30));
        // when
        int updateCount = memberRepository.bulkAgePlus(20);
//        em.clear();
        // then
        assertThat(updateCount).isEqualTo(3);

        Member member5 = memberRepository.findById(5L).orElse(null);
        System.out.println("member5.getAge() = " + member5.getAge());
    }
    
    @Test
    void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));
        em.flush();
        em.clear();
        //when
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");
        //then
        for (Member member : members) {
            System.out.println("Hibernate.isInitialized(member.getTeam()) = " + Hibernate.isInitialized(member.getTeam()));
            // 지연 로딩 여부 확인

            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println(member.getTeam().getTeamName());
        }
    }

    @Test
    void callCustom() {
        List<Member> all = memberRepository.findAll();
    }

    @Test
    void ProjectionTest() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 1, teamA);
        Member m2 = new Member("m2", 1, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        List<NestedClosedProjection> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjection.class);
        // then
        for (NestedClosedProjection usernameOnly : result) {
            System.out.println("usernameOnly.getUsername() = " + usernameOnly.getUsername());
            System.out.println("usernameOnly.getUsername() = " + usernameOnly.getTeam().getTeamName());
        }

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void NativeQueryTest() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 1, teamA);
        Member m2 = new Member("m2", 1, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();
        // when
        Member result = memberRepository.findByNativeQuery("m1");
        // then
        System.out.println("result = " + result);
    }

    @Test
    void NativeQueryProjectionTest() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 1, teamA);
        Member m2 = new Member("m2", 1, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();
        // when
        Page<MemberProjection> result = memberRepository.findByMemberProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection);
        }
        // then
    }
}