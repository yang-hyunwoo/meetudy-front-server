package front.meetudy.user.controller.common;

import front.meetudy.annotation.customannotation.CurrentMember;
import front.meetudy.config.chatinterceptor.StudyGroupAuthValidator;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.response.file.FileResDto;
import front.meetudy.user.service.common.file.FilesService;
import front.meetudy.util.MessageUtil;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.time.Duration;
import java.util.List;


//TODO 확장자 체크 로직 추가 하기

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@Tag(name="파일 관련 컨트롤러" , description = "FilesController")
@Slf4j
public class FilesController {

    private final FilesService filesService;

    private final StudyGroupAuthValidator studyGroupAuthValidator;

    private final MessageUtil messageUtil;

    @Operation(summary = "파일 업로드" , description = "파일 업로드")
    @PostMapping("/file-upload")
    public ResponseEntity<Response<FileResDto>> fileUpload(
            @CurrentMember Member member,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(required = false) Long fileId
    ) {
        return Response.ok(messageUtil.getMessage("file.insert.ok"),
                filesService.createFilesGroup(member, fileId, files));
    }

    @Operation(summary = "파일 삭제", description = "파일 삭제")
    @PutMapping("file-delete")
    public ResponseEntity<Response<Void>> fileDelete(
            @CurrentMember Member member,
            @RequestParam(required = false) Long fileId,
            @RequestParam(required = false) List<Long> delFileDetailsId
    ) {
        if (delFileDetailsId != null && !delFileDetailsId.isEmpty()) {
            filesService.deleteFileDetail(member.getId(), fileId, delFileDetailsId);
        }
        return Response.update(messageUtil.getMessage("file.delete.ok"),
                null);
    }

    @Operation(summary = "파일 다운로드", description = "파일 다운로드")
    @GetMapping("/file/download")
    public void download(
            @RequestParam String url,
            @RequestParam Long studyGroupId,
            HttpServletResponse response,
            @CurrentMember Member member
    ) throws IOException {
        //그룹에 멤버가 속해잇는지 확인
        studyGroupAuthValidator.validateMemberInGroup(studyGroupId, member.getId());

        URL fileUrl = new URL(url);
        URLConnection conn = fileUrl.openConnection();

        // Content-Type 설정
        String contentType = conn.getContentType();
        response.setContentType(contentType != null ? contentType : "application/octet-stream");

        // 파일명 추출 (마지막 경로값)
        String path = fileUrl.getPath();
        String rawFileName = path.substring(path.lastIndexOf("/") + 1);
        String fileName = URLDecoder.decode(rawFileName, "UTF-8");

        // Content-Disposition: 브라우저가 다운로드하게끔 유도
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Content-Transfer-Encoding", "binary");

        // 파일 스트리밍
        try (InputStream in = conn.getInputStream(); OutputStream out = response.getOutputStream()) {
            StreamUtils.copy(in, out);
        }
    }

    /**
     * r2 cloudflare 파일 다운시 권한
     * 유효한 서명된 URL(presigned URL) 접근
     *
     * @param url
     * @param studyGroupId
     * @param member
     * @return
     */
    @GetMapping("/file/signed-url")
    public ResponseEntity<String> getDownloadUrl(
            @RequestParam String url,
            @RequestParam Long studyGroupId,
            @CurrentMember Member member
    ) {
        //그룹에 멤버가 속해잇는지 확인
        studyGroupAuthValidator.validateMemberInGroup(studyGroupId, member.getId());
        String signedUrl = filesService.generateSignedUrl(extractR2ObjectKeyFromUrl(url), Duration.ofMinutes(10));
        return ResponseEntity.ok(signedUrl);
    }

    private String extractR2ObjectKeyFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath(); // "/bucket-name/your-file-name.txt"
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid R2 file URL", e);
        }
    }

}
