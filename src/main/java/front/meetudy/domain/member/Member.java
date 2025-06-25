package front.meetudy.domain.member;

import front.meetudy.constant.member.MemberEnum;
import front.meetudy.constant.member.MemberProviderTypeEnum;
import front.meetudy.domain.common.BaseEntity;
import front.meetudy.exception.CustomApiException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Objects;

import static front.meetudy.constant.error.ErrorEnum.*;
import static front.meetudy.constant.login.LoginErrorCode.LG_PASSWORD_WRONG_LOCKED;
import static org.springframework.http.HttpStatus.*;


@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(name = "UK_member_email_nickname", columnNames = {"email", "provider"})
})

public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long profileImageId;

    @Column(nullable = false, length = 100)

    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 8)
    private String birth;

    @Column(length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    @ToString.Exclude
    private String password;

    @Column(nullable = false)
    private boolean isEmailAgreed;

    @Enumerated(EnumType.STRING)
    private MemberEnum role;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private MemberProviderTypeEnum provider;

    @Column(length = 100)
    private String providerId;

    @Column(nullable = false)
    private int failLoginCount;

    @Column(nullable = false)
    private boolean deleted;

    private LocalDateTime deletedAt;

    private LocalDateTime passwordChangeAt;


    @Builder
    protected Member(Long id,
                     Long profileImageId,
                     String email,
                     String name,
                     String nickname,
                     String birth,
                     String phoneNumber,
                     String password,
                     boolean isEmailAgreed,
                     MemberEnum role,
                     MemberProviderTypeEnum provider,
                     String providerId,
                     int failLoginCount,
                     boolean deleted,
                     LocalDateTime deletedAt,
                     LocalDateTime passwordChangeAt
    ) {
        this.id = id;
        this.profileImageId = profileImageId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.isEmailAgreed = isEmailAgreed;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.failLoginCount = failLoginCount;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
        this.passwordChangeAt = passwordChangeAt;
    }

    /**
     * 일반 사용자 생성 메서드
     *
     * @param profileImageId
     * @param email
     * @param name
     * @param nickname
     * @param birth
     * @param phoneNumber
     * @param password
     * @param isEmailAgreed
     * @return
     */
    public static Member createMember(Long profileImageId,
                                      String email,
                                      String name,
                                      String nickname,
                                      String birth,
                                      String phoneNumber,
                                      String password,
                                      boolean isEmailAgreed
    ) {
        return Member.builder()
                .profileImageId(profileImageId)
                .email(email)
                .name(name)
                .nickname(nickname)
                .birth(birth)
                .phoneNumber(phoneNumber)
                .password(password)
                .isEmailAgreed(isEmailAgreed)
                .role(MemberEnum.USER)
                .provider(MemberProviderTypeEnum.NORMAL)
                .failLoginCount(0)
                .deleted(false)
                .passwordChangeAt(LocalDateTime.now())
                .build();
    }

    /**
     * oauth 사용자 생성 메서드
     *
     * @param email
     * @param name
     * @param nickname
     * @param password
     * @param provider
     * @param providerId
     * @return
     */
    public static Member createOauthMember(String email,
                                           String name,
                                           String nickname,
                                           String password,
                                           MemberProviderTypeEnum provider,
                                           String providerId
    ) {
        return Member.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .password(password)
                .isEmailAgreed(false)
                .role(MemberEnum.USER)
                .provider(provider)
                .providerId(providerId)
                .failLoginCount(0)
                .deleted(false)
                .passwordChangeAt(LocalDateTime.now())
                .build();
    }

    /**
     * 사용자 인증 메서드
     *
     * @param id
     * @param role
     * @return
     */
    public static Member partialOf(Long id,
                                   MemberEnum role
    ) {
        return Member.builder()
                .id(id)
                .role(role)
                .build();
    }

    /**
     * 비밀번호 오류 횟수 증가
     */
    public void increaseFailLoginCount() {
        if (this.failLoginCount < 5) {
            this.failLoginCount++;
        } else {
            throw new CustomApiException(LG_PASSWORD_WRONG_LOCKED.getStatus(), ERR_007, LG_PASSWORD_WRONG_LOCKED.getMessage());
        }
    }

    /**
     * 비밀번호 오류 횟수 초기화
     */
    public void initLoginCount() {
        this.failLoginCount = 0;
    }

    /**
     * 비밀번호 변경 이벤트
     *
     * @param currentPw
     * @param newPw
     * @param passwordEncoder
     */
    public void passwordChange(String currentPw,
                               String newPw,
                               PasswordEncoder passwordEncoder
    ) {
        if (!passwordEncoder.matches(currentPw, this.password)) {
            throw new CustomApiException(BAD_REQUEST, ERR_022, ERR_022.getValue());
        }
        if (currentPw.equals(newPw)) {
            throw new CustomApiException(BAD_REQUEST, ERR_023, ERR_023.getValue());
        }
        this.password = passwordEncoder.encode(newPw);
    }

    /**
     * 멤버 상세 수정
     *
     * @param nickname
     * @param phoneNumber
     * @param profileImageId
     */
    public void memberDetailChange(String nickname,
                                   String phoneNumber,
                                   Long profileImageId
    ) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.profileImageId = profileImageId;
    }

    public void withdrawValid(String currentPw,
                              PasswordEncoder passwordEncoder
    ) {
        if (!passwordEncoder.matches(currentPw, this.password)) {
            throw new CustomApiException(BAD_REQUEST, ERR_022, ERR_022.getValue());
        }
    }

    /**
     * 멤버 탈퇴
     */
    public void memberWithdraw() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}

