package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryCustomImplTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void customTest() {
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }
}