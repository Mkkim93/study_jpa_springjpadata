package study.data_jpa.repository;

/**
 * 중첩 구조
 */

public interface NestedClosedProjections {

    String getUsername(); // left join 의 조인 대상은 문자열을 가져옴
    TeamInfo getTeam(); // 인터페이스 내부에서 조인으로 가져오는 Team 객체는 getName 으로 문자열을 가져오는게 아니라 객체 자제를 가져오기 때문에
    // team 의 데이터를 모두 가져옴 (최적화 x)

    interface TeamInfo {
        String getName();
    }
}
