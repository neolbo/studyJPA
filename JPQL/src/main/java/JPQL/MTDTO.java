package JPQL;

public class MTDTO {
    Member member;
    Team team;

    public MTDTO(Member member, Team team) {
        this.member = member;
        this.team = team;
    }

    public Member getMember() {
        return member;
    }

    public Team getTeam() {
        return team;
    }
}
