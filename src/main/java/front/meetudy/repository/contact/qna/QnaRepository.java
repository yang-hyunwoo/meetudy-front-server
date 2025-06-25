package front.meetudy.repository.contact.qna;

import front.meetudy.domain.contact.Qna.QnaBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//TODO 네이티브 쿼리 SELECT 전체 조회로 인한 DTO projects 또는 entity 쿼리 써야 ??..
public interface QnaRepository extends JpaRepository<QnaBoard, Long> {

    /**
     * 멤버가 작성한 qna 리스트 조회
     *
     * @param userId 멤버 id
     * @return 멤버가 작성한 qna 리스트 객체
     */
    @Query(value = "SELECT * FROM qna_board " +
            "WHERE question_user_id = :userId " +
            "ORDER BY id desc", nativeQuery = true)
    List<QnaBoard> findByQuestionUserIdNative(@Param("userId") Long userId);

}
