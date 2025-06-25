package front.meetudy.repository.querydsl.main;

import com.querydsl.jpa.impl.JPAQueryFactory;
import front.meetudy.domain.common.file.QFilesDetails;
import front.meetudy.domain.contact.notice.QNoticeBoard;
import front.meetudy.dto.response.main.MainNoticeResDto;
import front.meetudy.dto.response.main.QMainNoticeResDto;
import front.meetudy.repository.Main.MainQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MainQueryDslRepositoryImpl implements MainQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    QNoticeBoard noticeBoard = QNoticeBoard.noticeBoard;
    QFilesDetails filesDetails = QFilesDetails.filesDetails;

    /**
     * 메인 공지 사항 리스트 조회
     *
     * @return 메인 공지 사항 리스트 객체
     */
    @Override
    public List<MainNoticeResDto> findMainNotice() {
      return  queryFactory.select(new QMainNoticeResDto(
                        noticeBoard.id,
                        noticeBoard.noticeType,
                        noticeBoard.title,
                        noticeBoard.summary,
                        filesDetails.fileUrl
                ))
                .from(noticeBoard)
                .leftJoin(filesDetails)
                .on(noticeBoard.thumbnailFile.id.eq(filesDetails.files.id).and(filesDetails.deleted.eq(false)))
                .where(noticeBoard.deleted.isFalse()
                        .and(noticeBoard.visible.isTrue()))
                .orderBy(noticeBoard.sort.asc())
                .limit(5)
                .fetch();
    }

}
