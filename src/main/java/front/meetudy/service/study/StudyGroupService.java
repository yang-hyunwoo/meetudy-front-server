package front.meetudy.service.study;

import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.constant.study.JoinStatusEnum;
import front.meetudy.constant.study.MemberRole;
import front.meetudy.domain.common.file.Files;
import front.meetudy.domain.member.Member;
import front.meetudy.domain.study.StudyGroup;
import front.meetudy.domain.study.StudyGroupMember;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.study.StudyGroupCreateReqDto;
import front.meetudy.dto.request.study.StudyGroupJoinReqDto;
import front.meetudy.dto.request.study.StudyGroupOtpReqDto;
import front.meetudy.dto.request.study.StudyGroupPageReqDto;
import front.meetudy.dto.response.study.StudyGroupPageResDto;
import front.meetudy.dto.response.study.StudyGroupStatusResDto;
import front.meetudy.exception.CustomApiException;
import front.meetudy.repository.common.file.FilesRepository;
import front.meetudy.repository.study.StudyGroupDetailRepository;
import front.meetudy.repository.study.StudyGroupMemberRepository;
import front.meetudy.repository.study.StudyGroupQueryDslRepository;
import front.meetudy.repository.study.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;

    private final StudyGroupDetailRepository studyGroupDetailRepository;

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    private final FilesRepository filesRepository;

    private final StudyGroupQueryDslRepository studyGroupQueryDslRepository;


    public Long studySave(Member member, StudyGroupCreateReqDto studyGroupCreateReqDto) {
        studyGroupCreateCount(member);
        studyGroupCreatValidation(studyGroupCreateReqDto);

        Files files = null;
        if(getThumbnailFileChk(studyGroupCreateReqDto)) {
            files = filesRepository.findById(studyGroupCreateReqDto.getThumbnailFileId()).orElse(null);
        }

        StudyGroup entity = studyGroupCreateReqDto.toStudyGroupEntity(files);
        studyGroupRepository.save(entity);
        studyGroupDetailRepository.save(studyGroupCreateReqDto.toDetailEntity(entity));
        studyGroupMemberRepository.save(studyGroupCreateReqDto.toLeaderEntity(member, entity));

        return entity.getId();
    }

    public void joinStudyGroup(StudyGroupJoinReqDto studyGroupJoinReqDto, Member member) {

        //1.studygroup 존재 여부 확인
        StudyGroup studyGroup = studyGroupRepository.findValidStudyGroupById(studyGroupJoinReqDto.getStudyGroupId()).orElseThrow(() -> new CustomApiException(BAD_REQUEST, ERR_012, ERR_012.getValue()));
        //2.db 멤버 확인
        studyGroupMemberRepository.findByStudyGroupIdAndMemberId(studyGroup.getId(), member.getId()).ifPresent(
            user -> {
                throw new CustomApiException(BAD_REQUEST, ERR_003, ERR_003.getValue());
        });
        //3.저장
        studyGroupMemberRepository.save(studyGroupJoinReqDto.toEntity(member, studyGroup));
    }


    public PageDto<StudyGroupPageResDto> findStudyGroupListPage(Pageable pageable, StudyGroupPageReqDto studyGroupPageReqDto, Member member) {
        Page<StudyGroupPageResDto> studyGroupListPage = studyGroupQueryDslRepository.findStudyGroupListPage(pageable, studyGroupPageReqDto, member);
        return PageDto.of(studyGroupListPage, Function.identity());
    }

    public List<StudyGroupStatusResDto> findStudyGroupStatus(List<Long> studyGroupId, Member member) {
        return studyGroupQueryDslRepository.findStudyGroupStatus(studyGroupId, member);
    }

    public boolean existsByGroupIdAndOtp(StudyGroupOtpReqDto studyGroupOtpReqDto) {
        int count = studyGroupDetailRepository.existsByGroupIdAndOtp(studyGroupOtpReqDto.getStudyGroupId(), studyGroupOtpReqDto.getOptNumber());
        return count != 0;
    }


    /**
     * 그룹 생성 5개 체크
     * @param member
     */
    private void studyGroupCreateCount(Member member) {
        int studyGroupCreateCount = studyGroupQueryDslRepository.findStudyGroupCreateCount(member);
        if(studyGroupCreateCount>5) {
            throw new CustomApiException(BAD_REQUEST, ERR_019, ERR_019.getValue());
        }
    }

    /**
     * 그룹 생성 유효성 검사
     * @param studyGroupCreateReqDto
     */
    private static void studyGroupCreatValidation(StudyGroupCreateReqDto studyGroupCreateReqDto) {
        if(LocalDate.parse(studyGroupCreateReqDto.getStartDate()).isAfter(LocalDate.parse(studyGroupCreateReqDto.getEndDate()))) {
            throw new CustomApiException(BAD_REQUEST, ERR_016, ERR_016.getValue());
        }
        if(LocalTime.parse(studyGroupCreateReqDto.getMeetingStartTime()).isAfter(LocalTime.parse(studyGroupCreateReqDto.getMeetingEndTime()))) {
            throw new CustomApiException(BAD_REQUEST, ERR_017, ERR_017.getValue());
        }

        if(studyGroupCreateReqDto.isSecret()) {
            if(studyGroupCreateReqDto.getSecretPassword().isBlank() || studyGroupCreateReqDto.getSecretPassword().length() !=6) {
                throw new CustomApiException(BAD_REQUEST, ERR_002, ERR_002.getValue());
            }
        }
    }


    /**
     * 썸네일 파일 체크
     * @param studyGroupCreateReqDto
     * @return
     */
    private static boolean getThumbnailFileChk(StudyGroupCreateReqDto studyGroupCreateReqDto) {
        return studyGroupCreateReqDto.getThumbnailFileId() != null;
    }

}
