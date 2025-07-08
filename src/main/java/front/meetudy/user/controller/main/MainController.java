package front.meetudy.user.controller.main;


import front.meetudy.user.dto.response.main.MainNoticeResDto;
import front.meetudy.user.dto.response.main.MainStudyGroupResDto;
import front.meetudy.user.service.main.MainService;
import front.meetudy.util.MessageUtil;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
@Tag(name = "메인 관리 API", description = "MainController")
@Slf4j
public class MainController {

    private final MainService mainService;

    private final MessageUtil messageUtil;

    @Operation(summary = "추천 그룹 조회", description = "추천 그룹 조회")
    @GetMapping("/study-group/list")
    public ResponseEntity<Response<List<MainStudyGroupResDto>>> mainStudyGroupList(
    ) {
        return Response.ok(messageUtil.getMessage("main.group.list.read.ok"),
                mainService.mainStudyGroupList());
    }

    @Operation(summary = "메인 공지 사항 조회", description = "메인 공지 사항 조회")
    @GetMapping("/notice/list")
    public ResponseEntity<Response<List<MainNoticeResDto>>> mainNoticeList(
    ) {
        return Response.ok(messageUtil.getMessage("main.notice.list.read.ok"),
                mainService.mainNoticeList());
    }

}
