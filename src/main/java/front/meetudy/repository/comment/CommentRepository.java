package front.meetudy.repository.comment;

import front.meetudy.domain.comment.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"member"})
    @Query("SELECT c FROM Comment c " +
            "WHERE c.targetType =:targetType " +
            "AND c.deleted = false " +
            "ORDER BY c.id desc")
    List<Comment> findCommentList(String targetType);

    @EntityGraph(attributePaths = {"member"})
    @Query("SELECT c FROM Comment c " +
            "WHERE c.targetType =:targetType " +
            "AND c.targetId =:targetId " +
            "AND c.deleted = false " +
            "ORDER BY c.id desc")
    List<Comment> findCommentBoardList(String targetType , Long targetId);

    Optional<Comment> findByIdAndDeleted(Long id, boolean deleted);

}
