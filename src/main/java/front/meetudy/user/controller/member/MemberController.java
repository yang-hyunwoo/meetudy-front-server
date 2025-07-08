package front.meetudy.user.controller.member;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.docs.join.JoinValidationErrorExample;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.request.member.JoinMemberReqDto;
import front.meetudy.user.dto.response.member.JoinMemberResDto;
import front.meetudy.user.dto.response.member.LoginResDto;
import front.meetudy.user.service.member.MemberService;
import front.meetudy.util.MessageUtil;
import front.meetudy.util.cookie.CustomCookie;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "회원 관리 Controller", description = "MemberController")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    private final MessageUtil messageUtil;

    @Operation(summary = "회원가입 요청", description = "회원가입 시 단일 유효성 실패 예시 제공")
    @JoinValidationErrorExample
    @PostMapping("/join")
    public ResponseEntity<Response<JoinMemberResDto>> join(
            @RequestBody JoinMemberReqDto joinReqDto
    ) {
        JoinMemberResDto join = memberService.join(joinReqDto);
        ResponseCookie cookie = CustomCookie.createCookie("join_success", "success", 30);
        return Response.ok(cookie.toString(), messageUtil.getMessage("member.join.insert.ok"), join);
    }

    @Operation(summary = "로그인 여부 체크", description = "로그인 여부 체크")
    @GetMapping("/user/me")
    public ResponseEntity<Response<LoginResDto>> getUser(
            @CurrentMember Member member
    ) {
        if(member == null) {
            return Response.ok(messageUtil.getMessage("member.login.chk.no"), null);
        }
        return Response.ok(messageUtil.getMessage("member.login.chk.ok"),new LoginResDto(member));
    }

}
