package front.meetudy.controller.mypage;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.constant.security.CookieEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.mypage.MypageDetailChgReqDto;
import front.meetudy.dto.request.mypage.MypagePwdChgReqDto;
import front.meetudy.dto.request.mypage.MypageWithdrawReqDto;
import front.meetudy.dto.response.mypage.MyPageGroupCountResDto;
import front.meetudy.dto.response.mypage.MyPageMemberResDto;
import front.meetudy.service.mypage.MyPageService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static front.meetudy.util.cookie.CustomCookie.deleteCookie;

@RestController
@RequestMapping("/api/private/mypage")
@RequiredArgsConstructor
@Tag(name = "마이페이지 관리 API", description = "MypageController")
@Slf4j
public class MypageController {

    private final MyPageService myPageService;

    @Operation(summary = "멤버 상세 조회 ", description = "멤버 상세 조회")
    @GetMapping("/profile/detail")
    public ResponseEntity<Response<MyPageMemberResDto>> memberDetail(
            @CurrentMember Member member
            ) {
        return Response.ok("멤버 상세 조회 완료", myPageService.memberDetail(member));
    }

    @Operation(summary = "멤버 그룹 갯수 조회 ", description = "멤버 그룹 갯수 조회")
    @GetMapping("/group/count")
    public ResponseEntity<Response<MyPageGroupCountResDto>> memberGroupCount(
            @CurrentMember Member member
    ) {
        return Response.ok("멤버 상세 조회 완료", myPageService.memberGroupCount(member));
    }

    @Operation(summary = "멤버 상세 수정", description = "멤버 상세 수정")
    @PutMapping("/profile/detail/update")
    public ResponseEntity<Response<Void>> memberDetailChange(
            @RequestBody MypageDetailChgReqDto mypageDetailChgReqDto,
            @CurrentMember Member member
    ) {
        myPageService.memberDetailChange(member, mypageDetailChgReqDto);
        return Response.update("멤버 상세 수정 완료", null);
    }

    @Operation(summary = "멤버 비밀번호 변경", description = "멤버 비밀번호 변경")
    @PutMapping("/profile/pwd-change")
    public ResponseEntity<Response<Void>> memberPwdChange(
            @RequestBody MypagePwdChgReqDto mypagePwdChgReqDto,
            @CurrentMember Member member
    ) {
        myPageService.changePassword(member, mypagePwdChgReqDto);
        return Response.update("멤버 비밀번호 변경 완료", null);
    }


    @Operation(summary = "멤버 삭제 ", description = "멤버 삭제")
    @PutMapping("/profile/withdraw")
    public ResponseEntity<Response<Void>> memberWithdraw(
            HttpServletResponse response,
            @RequestBody MypageWithdrawReqDto mypageWithdrawReqDto,
            @CurrentMember Member member
    ) {
        myPageService.memberWithdraw(member,mypageWithdrawReqDto);
        response.addHeader("Set-Cookie", deleteCookie(CookieEnum.accessToken.getValue()).toString());
        response.addHeader("Set-Cookie", deleteCookie(CookieEnum.refreshToken.getValue()).toString());
        response.addHeader("Set-Cookie", deleteCookie(CookieEnum.isAutoLogin.getValue()).toString());
        return Response.delete("멤버 삭제 완료", null);
    }


}
