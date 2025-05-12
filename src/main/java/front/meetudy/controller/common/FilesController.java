package front.meetudy.controller.common;

import com.cloudinary.Cloudinary;
import front.meetudy.auth.LoginUser;
import front.meetudy.domain.common.file.Files;
import front.meetudy.dto.response.file.FileResDto;
import front.meetudy.service.common.CloudinaryService;
import front.meetudy.service.common.file.FilesService;
import front.meetudy.util.response.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


//TODO 확장자 체크 하기!

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@Tag(name="파일 관련 컨트롤러" , description = "FilesController")
@Slf4j
public class FilesController {

    private final FilesService filesService;


    @PostMapping("/file-upload")
    public ResponseEntity<Response<FileResDto>> fileUpload(@AuthenticationPrincipal LoginUser loginUser,
                                                           @RequestParam("files") List<MultipartFile> files,
                                                           @RequestParam(required = false) Long fileId,
                                                           @RequestParam(required = false) List<Long> delFileDetailsId) {

        //files 생성
        Files filesGroup = filesService.createFilesGroup(loginUser.getMember(),fileId);

        for (MultipartFile file : files) {
            String contentType = file.getContentType();

            if (contentType != null && contentType.startsWith("image/")) {
                //cloudinary 이미지 저장
                filesService.uploadImageCloudinary(filesGroup, file);
            } else {
                //cloudFlare 이미지 제외 저장
                filesService.uploadEtcCloudflare(filesGroup, file);
            }
        }
        deletedFilePresent(delFileDetailsId);
        return Response.ok("업로드 완료", FileResDto.from(filesGroup));
    }

    private void deletedFilePresent(List<Long> delFileDetailsId) {
        if (delFileDetailsId != null && !delFileDetailsId.isEmpty()) {
            filesService.deleteFileDetail(delFileDetailsId);
        }
    }

}
