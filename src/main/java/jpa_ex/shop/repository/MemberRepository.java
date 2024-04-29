package jpa_ex.shop.repository;

import jakarta.persistence.EntityManager;
import jpa_ex.shop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

/*
    @PersistenceContext
    private EntityManager em;
*/

    /**
     *  em 이 생성자를 통해 의존성을 주입 받을 때 싱글톤으로 주입 받아진다.
     *                          ==>> 동시성 문제 보장 x
     *
     *  그러나 스프링 프레임워크는 실제 em 을 주입하는 것이 아닌 '가짜' em 을 주입해
     *  동시성 문제를 해결한다.
     */
    private final EntityManager em;


    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
