package study.datajpa.repository;

import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsername(String username);

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.teamName) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    // 유연한 반환 타입 지원
    List<Member> findListByUsername(String name);

    Member findMemberByUsername(String name);

    Optional<Member> findOptionalByUsername(String name);

    Page<Member> findByAge(int age, Pageable pageable);     // 반환 값 Page 일 경우 =>> count query 실행

    Slice<Member> findSliceByAge(int age, Pageable pageable);        // count query 실행 x
    // limit + 1 을 조회하여 다음 페이지 여부 확인

    List<Member> findTop3ByAge(int age);

    List<Member> findFirst4ByAge(int age);

    @Query(value = "select m from Member m join m.team t",
            countQuery = "select m from Member m")
    Page<Member> findMemberAllCountBy(Pageable pageable);       // join 할 필요 없는 count query 분리하여 최적화

    @Modifying(clearAutomatically = true)       // @Query 를 통해 변경이 일어날 때 사용(insert, update, delete 등) (벌크성)
    // clearAutomatically = true 시 영속성 1차 캐시 자동 clear
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Override
    @EntityGraph(attributePaths = {"team"})
        // fetch join 간단히 해줌        // 복잡해지면 직접 jqpl 작성
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
        // EntityGraph 만 붙이면 fetch join 됨
    List<Member> findByAge(@Param("age") int age);      // 이런것도 가능

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    List<Member> findReadOnlyByUsername(String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"),
            forCounting = true)
        // page 반환 시 count query 힌트 적용 (기본값 = true)
    Page<Member> findPageReadOnlyByUsername(String username, Pageable pageable);

    // 제네릭 타입으로도 가능
    <T> List<T> findProjectionsByUsername(String username, Class<T> type);

    // native query
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    // native query Projection 활용 ==> page 도 가능
    @Query(value = "select m.member_id as id, m.username, t.teamName " +
            "from member m left join team t on m.team_id = t.team_id",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByMemberProjection(Pageable pageable);
}
