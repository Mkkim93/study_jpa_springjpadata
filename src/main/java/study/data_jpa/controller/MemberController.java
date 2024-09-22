package study.data_jpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.data_jpa.dto.MemberDTO;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    // @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }

    // @PageableDefault(size = 5, sort = "username") == 해당 메서드만 pageable setting 할 수 있음!!
    // 게시판에서 써먹자
    @GetMapping("/members")
    public Page<MemberDTO> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
       /* Page<Member> page = memberRepository.findAll(pageable);
        return page.map(member ->
                new MemberDTO(member.getId(), member.getUsername(), null));*/
        PageRequest pageRequest = PageRequest.of(1, 2);
        Page<MemberDTO> map = memberRepository.findAll(pageable)
                .map(MemberDTO::new);
        return map;
    }
}
