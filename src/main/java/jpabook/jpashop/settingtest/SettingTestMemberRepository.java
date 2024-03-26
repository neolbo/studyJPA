package jpabook.jpashop.settingtest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class SettingTestMemberRepository {
    @PersistenceContext
    EntityManager em;

    public Long save(SettingTestMember settingTestMember) {
        em.persist(settingTestMember);
        return settingTestMember.getId();
    }

    public SettingTestMember find(Long id) {
        return em.find(SettingTestMember.class, id);
    }
}
