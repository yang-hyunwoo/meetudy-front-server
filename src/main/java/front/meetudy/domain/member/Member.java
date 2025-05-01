package front.meetudy.domain.member;

import front.meetudy.constant.member.MemberEnum;
import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.exception.CustomApiException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static front.meetudy.exception.login.LoginErrorCode.LG_PASSWORD_WRONG_LOCKED;

@NoArgsConstructor  //스프링이 User 객체 생성시 빈 생성자로 new를 함
@Getter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(name = "UK_member_email_nickname", columnNames = {"email", "provider"})
})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long profileImageId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    private String birth;

    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    private boolean isEmailAgreed;

    @Enumerated(EnumType.STRING)
    private MemberEnum role;

    private MemberProviderTypeEnum provider;

    private String providerId;

    @Column(nullable = false)
    private int failLoginCount;

    @Column(nullable = false)
    private boolean isDeleted;

    private LocalDateTime deletedAt;

    private LocalDateTime passwordChangeAt;

    @Builder
    public Member(Long id,
                  String name,
                  String password,
                  String email,
                  MemberEnum role,
                  MemberProviderTypeEnum provider,
                  String providerId,
                  boolean isUsed,
                  LocalDateTime lastAccessDate,
                  LocalDateTime pwChgDate) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    public void increaseFailLoginCount() {
        if (this.failLoginCount < 5) {
            this.failLoginCount++;
        } else {
            throw new CustomApiException(LG_PASSWORD_WRONG_LOCKED.getStatus(),LG_PASSWORD_WRONG_LOCKED.getMessage());
        }
    }
}

