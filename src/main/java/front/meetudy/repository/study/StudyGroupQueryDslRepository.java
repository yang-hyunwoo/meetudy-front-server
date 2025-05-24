package front.meetudy.repository.study;

import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.study.StudyGroupPageReqDto;
import front.meetudy.dto.response.study.StudyGroupDetailResDto;
import front.meetudy.dto.response.study.StudyGroupPageResDto;
import front.meetudy.dto.response.study.StudyGroupStatusResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudyGroupQueryDslRepository {

    Page<StudyGroupPageResDto> findStudyGroupListPage(Pageable pageable, StudyGroupPageReqDto studyGroupPageReqDto, Member member);

    Optional<StudyGroupDetailResDto> findStudyGroupDetail(Long studyGroupId);

    List<StudyGroupStatusResDto> findStudyGroupStatus(List<Long> studyGroupId, Member member);

    int findStudyGroupCreateCount(Member member);
}
