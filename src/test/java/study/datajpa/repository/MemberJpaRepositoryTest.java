package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;
    @Autowired
    EntityManager em;

    @Test
    void testMemberJpaRepository() {
        // given
        Member member = new Member("memberA", 10);
        Member savedMember = memberJpaRepository.save(member);
        // when
        Member findMember = memberJpaRepository.find(member.getId());
        // then
        assertThat(findMember).isEqualTo(member);
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("userA", 10);
        Member member2 = new Member("userB", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deleteCount = memberJpaRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    void PagingTest() {
        // given
        memberJpaRepository.save(new Member("user1", 10));
        memberJpaRepository.save(new Member("user2", 10));
        memberJpaRepository.save(new Member("user3", 10));
        memberJpaRepository.save(new Member("user4", 10));
        memberJpaRepository.save(new Member("user5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;
        // when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        // then
        assertThat(members.size()).isEqualTo(4);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    @Rollback(value = false)
    void bulkTest() {
        // given
        memberJpaRepository.save(new Member("user1", 10));
        memberJpaRepository.save(new Member("user2", 19));
        memberJpaRepository.save(new Member("user3", 20));
        memberJpaRepository.save(new Member("user4", 21));
        memberJpaRepository.save(new Member("user5", 30));
        // when
        int updateCount = memberJpaRepository.bulkAgePlus(20);
        em.clear();
        // then
        assertThat(updateCount).isEqualTo(3);

        Member member5 = memberJpaRepository.find(5L);
        System.out.println("member5.getAge() = " + member5.getAge());
    }
}