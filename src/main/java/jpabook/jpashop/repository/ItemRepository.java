package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {     // 새로 등록하는 경우 id == null  ==> persist
            em.persist(item);
        } else {                        // 이미 등록되어 있으므로 merge 사용 (update..?)
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }
}
