package jpa_ex.shop.repository;

import jpa_ex.shop.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional
    void test() {
        // given
        Member member = new Member();
//        member.setName("userA");
        // when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.findOne(saveId);
        // then
        assertThat(findMember).isEqualTo(member);
    }
}