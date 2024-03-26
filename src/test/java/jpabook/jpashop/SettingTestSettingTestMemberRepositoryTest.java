package jpabook.jpashop;

import jpabook.jpashop.settingtest.SettingTestMember;
import jpabook.jpashop.settingtest.SettingTestMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SettingTestSettingTestMemberRepositoryTest {

    @Autowired
    SettingTestMemberRepository settingTestMemberRepository;

    @Test
    @DisplayName("멤버 테스트")
    @Transactional
    @Rollback(value = false)
    void testMember() {
        // given
        SettingTestMember settingTestMember = new SettingTestMember();
        settingTestMember.setUsername("memberA");

        // when
        Long saveId = settingTestMemberRepository.save(settingTestMember);
        SettingTestMember findSettingTestMember = settingTestMemberRepository.find(saveId);

        // then
        assertThat(findSettingTestMember.getId()).isEqualTo(settingTestMember.getId());
        assertThat(findSettingTestMember.getUsername()).isEqualTo(settingTestMember.getUsername());
        assertThat(findSettingTestMember).isEqualTo(settingTestMember);
    }
}