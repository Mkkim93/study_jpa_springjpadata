package study.data_jpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush(); // 영속성 컨텍스트를 비우기 위해 영속성컨텍스트에 있는 데이터를 db 로 날린다
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.Team = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws Exception {
        // given
        Member member = new Member("member1");
        memberRepository.save(member);
        Thread.sleep(100);
        member.setUsername("member2");
        em.flush(); // @PreUpdate
        em.clear();

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        // then
        /*System.out.println("findMember. = " + findMember.getCreatedDate());
        System.out.println("findMember.getUpdatedDate() = " + findMember.getLastModifiedDate());
        System.out.println("findMember.getCreateBy() = " + findMember.getCreateBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());*/
    }

}