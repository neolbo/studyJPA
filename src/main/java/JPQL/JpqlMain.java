package JPQL;

import jakarta.persistence.*;

import java.util.List;

public class JpqlMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPQL");
        EntityManager em = emf.createEntityManager();
        //code

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("A");
            member.setAge(10);
            em.persist(member);

            Query query = em.createQuery("select m from Member as m");

            TypedQuery<Member> query1 = em.createQuery("select m from Member as m", Member.class);


            TypedQuery<Member> query2 = em.createQuery("select m from Member as m where username = :username", Member.class);
            String userNameParam = "nameValue";
            query2 = query2.setParameter("username", userNameParam);
            List<Member> resultList = query2.getResultList();
            if (resultList.isEmpty()) {
                System.out.println("resultList: " + resultList);
                System.out.println("getResultList() 는 값이 없어도 noResultException 발생 x, list : null");
            }


            // getSingleResult() 는 값이 없으면 NoResultException, 2 이상이면 NonUniqueResultException 발생
            try {
                Member find = em.createQuery("select m from Member as m where id = :id", Member.class)
                        .setParameter("id", 2L)
                        .getSingleResult();
            } catch (NoResultException e) {
                e.printStackTrace();
            }

            Member member2 = new Member();
            member2.setUsername("A");
            member2.setAge(20);
            em.persist(member2);

            try{
                Member singleResult = em.createQuery("select m from Member as m where username = :username", Member.class)
                        .setParameter("username", "A")
                        .getSingleResult();
            } catch (NonUniqueResultException e) {
                e.printStackTrace();
            }

            // count(m), sum(m.age), avg(m.age), max(m.age), min(m.age) 가능
            em.createQuery("select count(m), sum(m.age), avg(m.age), max(m.age), min(m.age) from Member as m");

            // 값 타입으로 반환 가능
            Order order = new Order();
            order.setAddress(new Address("cityy", "streett", "zipcodee"));
            em.persist(order);

            Address ad = em.createQuery("select o.address from Order o where id = :id", Address.class)
                    .setParameter("id", 1L)
                    .getSingleResult();
            System.out.println("ad.getCity(): " + ad.getCity());
            System.out.println("ad.getStreet(): " + ad.getStreet());
            System.out.println("ad.getZipcode(): " + ad.getZipcode());


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}
