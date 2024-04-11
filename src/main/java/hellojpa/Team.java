package hellojpa;

import jakarta.persistence.*;

@Entity
@SequenceGenerator(name = "team_seq_generator", sequenceName = "team_seq")
public class Team {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "team_id")
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
