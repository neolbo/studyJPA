package jpabook.jpashop.settingtest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class SettingTestMember {

    @Id @GeneratedValue
    private Long id;
    private String username;
}
