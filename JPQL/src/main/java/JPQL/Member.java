package JPQL;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "Member.findByUsername", query="select m from Member m where m.username = :username"),
        @NamedQuery(
                name = "Member.findByAge", query = "select m from Member m where m.age = :age")
})
public class Member {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "member")
    private List<Order> orderList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    // 연관관계 편의 메서드
    public void changeTeam(Team team) {
        this.team = team;
        team.getMemberList().add(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
