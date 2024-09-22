package study.data_jpa.repository;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

public class MemberSpec {

    public static Specification<Member> teamName(final String teamName) {
        return (root, query, builder) -> {
            // root : 처음 찍은 Entity
            if (StringUtils.isEmpty(teamName)) {
                return null;
            }
            Join<Member, Team> t = root.join("team", JoinType.INNER);// 회원과 inner join
            return builder.equal(t.get("name"), teamName);
        };
    }

    public static Specification<Member> username(final String username) {
        return (Specification<Member>) (root, query, builder) ->
            builder.equal(root.get("username"), username);
    }
}
