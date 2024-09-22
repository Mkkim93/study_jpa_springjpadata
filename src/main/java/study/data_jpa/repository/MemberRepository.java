package study.data_jpa.repository;

import jakarta.persistence.Lob;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDTO;
import study.data_jpa.entity.Member;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// JpaRepository 를 상속받은 MemberRepository 는 자동으로 스프링 빈에 등록된다.
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {

    // 신기..
    // JPA 에서 메서드를 해석해서 쿼리 날려줌;;
    // 메서드명에서 camel 표기법 맞춰야됨
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3By();

    @Query(name = "Member.findByUsername")
    List<Member> findByUserName(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // DTO 조회 = dto 의 생성자로 조회한다는 느낌, 생성자의 파라미터값을 다 적어줘야함
    @Query("select new study.data_jpa.dto.MemberDTO(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDTO> findMemberDto();

    // in : collection 파라미터 값
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 (Optional)

    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m") // count 쿼리를 분리
    Page<Member> findByAge(int age, Pageable pageable);

//    Slice<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) // jpa 의 excuteUpdate() 와 같음
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // fetch join : member 를 조인할 때 연관된 쿼리를 한번에 다 끌고온다. n + 1 문제 해결
    @Query("select m from Member m left join fetch m.team") // member 를 조회할 때 fetch 옆에 있는 team 을 한번에 끌고옴
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"}) // fetch join 을 위한 어노테이션
    List<Member> findAll();

    // jpql + EntityGraph
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // method 이름 + EntityGraph
//    @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // select for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String name);

    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    // 네이티브 쿼리 사용 (nativeQuery = true)
    @Query(value = "select * from Member m where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as " +
            "teamName from member m left join team t on m.team_id = t.team_id",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
