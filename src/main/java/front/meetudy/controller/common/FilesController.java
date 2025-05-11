package front.meetudy.controller.common;

import com.cloudinary.Cloudinary;
import front.meetudy.domain.common.file.Files;
import front.meetudy.service.common.CloudinaryService;
import front.meetudy.service.common.file.FilesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


//TODO 확장자 체크 하기! , 변경 시 상세 deleted 변경 하기

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name="파일 관련 컨트롤러" , description = "FilesController")
@Slf4j
public class FilesController {

    private final FilesService filesService;

    @PostMapping("/file-upload")
    public void fileUpload(@RequestParam("files") List<MultipartFile> files,Long fileId) {

        //files 생성
        Files filesGroup = filesService.createFilesGroup(fileId);

        for (MultipartFile file : files) {
            String contentType = file.getContentType();

            if(contentType != null && contentType.startsWith("image/")) {
                //cloudinary 이미지 저장
                filesService.uploadImageCloudinary(filesGroup,file);
            } else {
                //cloudFlare 이미지 제외 저장
                filesService.uploadEtcCloudflare(filesGroup,file);
            }
        }

    }

}
