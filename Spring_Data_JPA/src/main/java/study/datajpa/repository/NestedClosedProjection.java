package study.datajpa.repository;

public interface NestedClosedProjection {
    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo {
        String getTeamName();
    }

    /**
     *  프로젝션 대상이 root 엔티티면 JPQL Select 절 최적화 가능
     * 하지만 대상이 root 가 아니면 Left Outer Join 처리, root 가 아닌 엔티티 모든 필드 조회  
     */
}
