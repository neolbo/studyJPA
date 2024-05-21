package jpa_ex.shop.service;

import jpa_ex.shop.domain.Member;
import jpa_ex.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {
        // 동일한 이름이면 안된다고 가정
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * id로 멤버 조회
     */
    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }



    /**
     * @return member 로 생각하였지만 이것보다는
     * 'command 와 query 를 분리해서 짜는게 유지보수성 더 좋다'
     *
     * member 를 반환하면 결국 영속성이 끊긴 member 가 반환되고, 결국 update 하면서 member 를 query 하는 꼴
     * ==> pk 값만을 반환하든 void 로 딱 command 만 있게 만듦
     */
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
