package front.meetudy.controller.member;

import front.meetudy.docs.join.JoinValidationErrorExample;
import front.meetudy.dto.request.member.JoinMemberReqDto;
import front.meetudy.dto.response.member.JoinMemberResDto;
import front.meetudy.service.member.MemberService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "회원관련", description = "MemberController")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입 요청", description = "회원가입 시 단일 유효성 실패 예시 제공")
    @JoinValidationErrorExample
    @PostMapping("/join")
    public ResponseEntity<Response<JoinMemberResDto>> join(@RequestBody JoinMemberReqDto joinReqDto ) {
        JoinMemberResDto join = memberService.join(joinReqDto);
        return Response.ok("회원가입이 완료 되었습니다.", join);

    }

}
