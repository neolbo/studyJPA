package jpa_ex.shop.service;

import jakarta.persistence.EntityManager;
import jpa_ex.shop.domain.Member;
import jpa_ex.shop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    EntityManager em;

    @Test
    void 회원가입() {
        // given
        Member member = new Member();
        member.setName("AAA");
        // when
        Long memberId = memberService.join(member);
        // then
        assertThat(memberRepository.findOne(memberId)).isEqualTo(member);
    }
    
    @Test
    void 중복_회원_예외() {
        // given
        Member member = new Member();
        member.setName("AAA");
        memberService.join(member);

        Member member2 = new Member();
        member2.setName("AAA");
        // when
        // then
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));

        // error 메세지 확인
        assertEquals("이미 존재하는 회원입니다.", e.getMessage());
    }
}