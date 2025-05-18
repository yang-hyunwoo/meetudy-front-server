package front.meetudy.repository.comment;

import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.comment.Comment;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.comment.CommentWriteReqDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;
    Member member;
    @BeforeEach
    void setUp() {
        member = Member.createMember(null, "test@naver.com", "테스트", "테스트", "19950120", "01011112222", "test", false);
        Member persist = em.persist(member);
        em.persist(Comment.createComments(member,"freeboard",1L,"111",false));
        em.persist(Comment.createComments(member,"freeboard",1L,"111",false));
        em.persist(Comment.createComments(member,"freeboard",1L,"111",false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("댓글 조회")
    void comment_search() {
        List<Comment> freeboard = commentRepository.findCommentList("freeboard");
        assertThat(freeboard.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("댓글 저장")
    void comment_save() {
        CommentWriteReqDto commentWriteReqDto = new CommentWriteReqDto("freeboard", 1L, "댓글1");
        Comment entity = commentWriteReqDto.toEntity(member);
        Comment save = commentRepository.save(entity);
        assertThat(save.getContent()).isEqualTo("댓글1");
        assertThat(save.getTargetType()).isEqualTo("freeboard");

    }

}