package study.data_jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = PROTECTED)
@ToString(of = {"id", "name"})
public class Team {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team") // 양방향 관계 일 때 mappedBy 적용 = 실제 db 테이블에 외래키가 없는 엔티티쪽에 mappedBy 를 걸어준다.
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
