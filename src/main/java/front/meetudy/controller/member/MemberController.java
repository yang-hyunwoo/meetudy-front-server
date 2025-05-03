package front.meetudy.controller.member;

import front.meetudy.dto.request.member.JoinMemberReqDto;
import front.meetudy.dto.response.member.JoinMemberResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.service.member.MemberService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "회원관련", description = "MemberController")
public class MemberController {

    private final MemberService memberService;

    //    @Operation(summary = "사용자 회원 가입", description = "사용자 회원 가입", tags = { "MemberController" })
    @Operation(summary = "사용자 회원 가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "400", description = "회원 가입 실패", content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/join")
    public ResponseEntity<Response<JoinMemberResDto>> join(@RequestBody @Valid JoinMemberReqDto joinReqDto ) {
        JoinMemberResDto join = memberService.join(joinReqDto);
        return Response.ok("생성", join);

    }


    }



