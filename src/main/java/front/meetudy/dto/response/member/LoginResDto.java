package front.meetudy.dto.response.member;

import front.meetudy.domain.member.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LoginResDto {

    private Long id;
    private String name;
    private String createdAt;
    private boolean passwordExpired;

    public LoginResDto(Member member) {

        this.id = member.getId();
        this.name = member.getName();
    }
}
