package front.meetudy.controller.mypage;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.constant.security.CookieEnum;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.mypage.MypageDetailChgReqDto;
import front.meetudy.dto.request.mypage.MypageMessageWriteReqDto;
import front.meetudy.dto.request.mypage.MypagePwdChgReqDto;
import front.meetudy.dto.request.mypage.MypageWithdrawReqDto;
import front.meetudy.dto.response.mypage.MyPageMessageResDto;
import front.meetudy.dto.response.mypage.MyPageBoardWriteResDto;
import front.meetudy.dto.response.mypage.MyPageGroupCountResDto;
import front.meetudy.dto.response.mypage.MyPageMemberResDto;
import front.meetudy.service.mypage.MyPageService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @Operation(summary = "멤버가 작성한 게시판 리스트 조회" , description ="멤버가 작성한 게시판 리스트 조회")
    @GetMapping("/board/list")
    public ResponseEntity<Response<PageDto<MyPageBoardWriteResDto>>> memberBoardWriteList(
            @PageableDefault(size = 5, page = 0) Pageable pageable,
            @CurrentMember Member member
    ) {
        return Response.ok("멤버가 작성한 게시판 리스트 조회 성공", myPageService.memberBoardWriteList(member, pageable));
    }

    @Operation(summary = "받은 쪽지함 리스트 조회" , description = "쪽지함 리스트 조회")
    @GetMapping("/message/receive/list")
    public ResponseEntity<Response<PageDto<MyPageMessageResDto>>> receiveMessageList(
            @PageableDefault(size = 5, page = 0) Pageable pageable,
            @CurrentMember Member member
    ) {
        return Response.ok("쪽지함 리스트 조회 완료", myPageService.receiveMessageList(member, pageable));
    }

    @Operation(summary = "보낸 쪽지함 리스트 조회" , description = "쪽지함 리스트 조회")
    @GetMapping("/message/send/list")
    public ResponseEntity<Response<PageDto<MyPageMessageResDto>>> sendMessageList(
            @PageableDefault(size = 5, page = 0) Pageable pageable,
            @CurrentMember Member member
    ) {
        return Response.ok("쪽지함 리스트 조회 완료", myPageService.sendMessageList(member, pageable));
    }

    @Operation(summary = "쪽지 전송", description = "쪽지 전송")
    @PostMapping("/message/send")
    public ResponseEntity<Response<Void>> messageSend(
            @RequestBody MypageMessageWriteReqDto mypageMessageWriteReqDto,
            @CurrentMember Member member
    ) {
        myPageService.messageSend(mypageMessageWriteReqDto, member);
        return Response.create("쪽지 전송 완료", null);
    }

    @Operation(summary = "쪽지 읽음" , description = "쪽지 읽음")
    @PutMapping("/message/{id}/read")
    public ResponseEntity<Response<Void>> messageRead(
            @PathVariable("id") Long messageId,
            @CurrentMember Member member
    ) {
        myPageService.messageRead(messageId, member);
        return Response.update("쪽지 읽음 완료", null);
    }

    @Operation(summary = "쪽지 삭제" , description = "쪽지 삭제")
    @PutMapping("/message/{id}/delete")
    public ResponseEntity<Response<Void>> messageDelete(
            @PathVariable("id") Long messageId,
            @CurrentMember Member member
    ) {
        myPageService.messageDelete(messageId, member);
        return Response.update("쪽지 삭제 완료", null);
    }
}
