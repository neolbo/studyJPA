package hellojpa.jpashop_Ex.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(name = "category_seq_generator", sequenceName = "category_seq")
public class Category extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    // Category - Item  ==>  ManyToMany 는 사용 x  ==> 1:n , n:1로 풀어 사용

    // 연관관계 편의 메서드
    public void addChild(Category child) {
        this.child.add(child);
        child.setParent(this);
    }

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

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChild() {
        return child;
    }

    public void setChild(List<Category> child) {
        this.child = child;
    }
}
