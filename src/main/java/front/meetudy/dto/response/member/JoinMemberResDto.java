package front.meetudy.dto.response.member;

import front.meetudy.domain.member.Member;
import lombok.Data;

@Data
public class JoinMemberResDto {

    private Long id;

    private String name;


    public JoinMemberResDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
    }
}
