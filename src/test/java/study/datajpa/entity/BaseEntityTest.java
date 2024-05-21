package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.TeamRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BaseEntityTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    EntityManager em;

    @Test
    void JpaBaseEntityTest() throws Exception {
        // given
        Member member = new Member("userA", 10);
        memberRepository.save(member);  // prePersist

        Thread.sleep(100);

        Team team = new Team("teamA");
        teamRepository.save(team);
        member.changeTeam(team);

        em.flush();     // preUpdate
        em.clear();

        // when
        Member findMember = memberRepository.findById(member.getId()).get();
        Team findTeam = teamRepository.findById(team.getId()).get();
        // then
        System.out.println("findMember.getCreatedTime() = " + findMember.getCreatedDate());
        System.out.println("findMember.getLastModifiedTime() = " + findMember.getLastModifiedDate());

        System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());

        System.out.println("findTeam.getCreatedDate() = " + findTeam.getCreatedDate());
        System.out.println("findTeam.getLastModifiedDate() = " + findTeam.getLastModifiedDate());
    }
}