package front.meetudy.service.common.file;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.domain.common.file.Files;
import front.meetudy.domain.common.file.FilesDetails;
import front.meetudy.domain.member.Member;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.common.file.FilesDetailsRepository;
import front.meetudy.repository.common.file.FilesRepository;
import front.meetudy.service.common.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static front.meetudy.constant.error.ErrorEnum.ERR_012;

@Service
@RequiredArgsConstructor
@Transactional
public class FilesService {

    private final FilesRepository filesRepository;

    private final CloudinaryService cloudinaryService;

    private final FilesDetailsRepository filesDetailsRepository;


    private final S3Client r2Client;

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    /**
     * fileId가 있다면 찾아서 반환 / 없다면 생성 후 반환
     * @param fileId
     * @return
     */
    public Files createFilesGroup(Member member,Long fileId) {
        if(fileId == null) {
            return filesRepository.save(Files.createFiles(member,false));
        } else {
            return filesRepository.findById(fileId).orElseThrow(()-> new CustomApiException(HttpStatus.BAD_REQUEST, ERR_012, ERR_012.getValue()));
        }
    }

    /**
     * cloudinary 업로드
     * @param group
     * @param file
     */
    public void uploadImageCloudinary(Files group , MultipartFile file) {
        Cloudinary cloudinary = cloudinaryService.connectCloudinary();
        String fileUrl;
        try {
            File tempFile = File.createTempFile("temp-", file.getOriginalFilename());
            file.transferTo(tempFile);
            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            var result = cloudinary.uploader().upload(
                    tempFile,
                    ObjectUtils.asMap("public_id", "myfolder/" + uniqueName,
                            "overwrite", false
                    )
            );
            tempFile.delete();
            fileUrl = result.get("secure_url").toString();
            String publicId = result.get("public_id").toString();
            FilesDetails filesDetails = FilesDetails.createFilesDetails(file.getOriginalFilename(),
                    uniqueName,
                    fileUrl ,
                    file.getSize() ,
                    file.getContentType() ,
                    publicId ,
                   false);
            group.addFileDetail(filesDetails);
            filesDetailsRepository.save(filesDetails);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * cloudflare 업로드
     * @param group
     * @param file
     */
    public void uploadEtcCloudflare(Files group , MultipartFile file) {
        try {
            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            r2Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(uniqueName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
            FilesDetails filesDetails = FilesDetails.createFilesDetails(file.getOriginalFilename(),
                    uniqueName,
                    "https://" + bucketName + "." + "42ff437d4dbd734cbcd48e9fd2156fbc" + ".r2.cloudflarestorage.com/" + uniqueName ,
                    file.getSize() ,
                    file.getContentType() ,
                    null ,
                    false);
            filesDetails.linkToFiles(group);
            filesDetailsRepository.save(filesDetails);
            // R2는 public endpoint 별도 세팅해야 함
        } catch (IOException e) {
            throw new RuntimeException("Cloudflare R2 업로드 실패", e);
        }
    }

    /**
     * filesDetails -> deleted 처리
     * @param delFileDetailsId
     */
    public void deleteFileDetail(List<Long> delFileDetailsId) {
        for (Long fileDetailId : delFileDetailsId) {
            filesDetailsRepository.findById(fileDetailId).ifPresent(FilesDetails::updateFileDeleted);
        }
    }
}
