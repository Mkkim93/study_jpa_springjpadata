package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false) // db 의 데이터베이스 컬럼 value 를 롤백하지 않는다.
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Autowired MemberRepository memberRepository;

    @Autowired EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());

        // validation
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    @DisplayName("검증")
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsername1() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

        result.forEach(System.out::println);
    }

    @Test
    public void findBy() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        Member m3 = new Member("AAA", 30);
        Member m4 = new Member("AAA", 40);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);

        List<Member> result = memberRepository.findTop3By();
        result.forEach(System.out::println);
    }

    @Test
    public void namedQueryTestFindByUsername() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);
        List<Member> result = memberJpaRepository.findByUsername("AAA");

        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void namedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUserName("AAA");

        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<String> usernameList = memberRepository.findUsernameList();
        usernameList.forEach(System.out::println);
    }

    @Test
    public void findTop3ByUserName() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUserNameAndAgeGreaterThen("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

        result.forEach(System.out::println);
    }

    @Test
    @DisplayName("페이징쿼리테스트")
    public void paging() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        // FirstResult 인 offset 의 인덱스 0 번 == db 튜플 1번째
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long count = memberJpaRepository.totalCount(age);

        assertThat(members.size()).isEqualTo(3);
        assertThat(count).isEqualTo(5);
        members.forEach(System.out::println);
        System.out.println("count = " + count);
    }

    @Test
    @DisplayName("(JPA) 벌크 쿼리 업데이트")
    public void bulkUpdate() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 40));

        int resultCount = memberJpaRepository.bulkAgePlus(20); // resultCount : 3

        List<Member> result = memberJpaRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    @DisplayName("벌크 쿼리 업데이트")
    public void MemberBulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int resultCount = memberRepository.bulkAgePlus(20); // resultCount : 3
        em.flush();
        em.clear();

        List<Member> result = memberRepository.findByUserName("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        assertThat(resultCount).isEqualTo(3);
    }



}