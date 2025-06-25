package front.meetudy.repository.comment;

import front.meetudy.domain.comment.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 댓글 리스트 조회
     *
     * @param targetType 댓글이 달린 게시판 종류
     * @return 댓글 리스트 객체
     */
    @EntityGraph(attributePaths = {"member"})
    @Query("SELECT c FROM Comment c " +
            "WHERE c.targetType =:targetType " +
            "AND c.deleted = false " +
            "ORDER BY c.id desc")
    List<Comment> findCommentList(String targetType);

    /**
     * 댓글 리스트 조회
     *
     * @param targetType 댓글이 달린 게시판 종류
     * @param targetId   댓글이 달린 게시판 id
     * @return 댓글이 달린 게시판 댓글 리스트 객체
     */
    @EntityGraph(attributePaths = {"member"})
    @Query("SELECT c FROM Comment c " +
            "WHERE c.targetType =:targetType " +
            "AND c.targetId =:targetId " +
            "AND c.deleted = false " +
            "ORDER BY c.id desc")
    List<Comment> findCommentBoardList(String targetType,
                                       Long targetId);

    /**
     * 댓글 삭제 여부 조회
     *
     * @param id      댓글 id
     * @param deleted 삭제 여부
     * @return 댓글 객체
     */
    Optional<Comment> findByIdAndDeleted(Long id,
                                         boolean deleted);

}
