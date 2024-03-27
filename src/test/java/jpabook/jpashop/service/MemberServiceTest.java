package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("회원가입")
//    @Rollback(value = false)
    void join() {
        // given
        Member member = new Member();
        member.setName("Jeong");
        // when
        Long saveId = memberService.join(member);
        // then
        assertEquals(member, memberService.findOne(saveId));
    }

    @Test
    @DisplayName("중복_회원_테스트")
    void duplicateTest() {
        // given
        Member member1 = new Member();
        member1.setName("Jeong");

        Member member2 = new Member();
        member2.setName("Jeong");
        // when
        memberService.join(member1);
        // then
        assertThrows(IllegalStateException.class, () -> memberService.join(member2));
    }

}