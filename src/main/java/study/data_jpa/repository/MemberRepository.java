package study.data_jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDTO;
import study.data_jpa.entity.Member;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// JpaRepository 를 상속받은 MemberRepository 는 자동으로 스프링 빈에 등록된다.
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이게되네..?
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

    // DTO 조회 = dto 의 생성자로 조회한다는 느낌 생성자의 파라미터값을 다 적어줘야함
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



}
