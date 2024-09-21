package study.data_jpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDTO;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = true)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @Test
    public void testMember() {
        System.out.println("memberRepository.getClass = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

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

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findUser() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        result.forEach(System.out::println);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    @DisplayName("DTO 조회")
    public void findMemberDto() {
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(teamA);
        memberRepository.save(m1);

        List<MemberDTO> memberDto = memberRepository.findMemberDto();
        memberDto.forEach(System.out::println);
    }

    @Test
    @DisplayName("in 사용")
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("CCC", 20);
        Member m4 = new Member("DDD", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);

        /*List<String> nameList = new ArrayList<>();
        nameList.add("AAA");
        nameList.add("BBB");
        List<Member> byNames = memberRepository.findByNames(nameList);*/

        List<Member> byNames = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        byNames.forEach(System.out::println);
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        // return Type 이 List<> 일 경우 입력하는 데이터가 없으면 반환이 되지 않는다 (result 값 : 0)
        List<Member> result = memberRepository.findListByUsername("asdf");
        System.out.println("result.size() = " + result.size());

        Member findMember = memberRepository.findMemberByUsername("asdf");
        System.out.println("findMember = " + findMember); // 결과가 없으면 null

        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("AAA");
        System.out.println("optionalMember = " + optionalMember); // 결과가 NULL 또는 있음
    }

    /*@Test
    @DisplayName("pageable 사용")
    public void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // Page<Member> page = memberRepository.findByAge(age, pageRequest);

        List<Member> content = page.getContent(); // page 내부에 있는 content 를 3개 꺼내옴
        long totalElements = page.getTotalElements();// == totalCount 과 동일
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }*/

    @Test
    @DisplayName("slice 사용")
    public void slice() {

        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        /**
         * !!!중요 : Entity 를 DTO 로 변환
         *  page.map() + 람다식으로 새로운 dto 객체 생성하여 인스턴스 생성 후 사용
         */

        // Entity 객체 -> memberDTO 객체로 변환
        Page<MemberDTO> toMap = page.map(member -> new MemberDTO(member.getId(), member.getUsername(), null));

        List<Member> content = page.getContent(); // page 내부에 있는 content 를 3개 꺼내옴

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }
}