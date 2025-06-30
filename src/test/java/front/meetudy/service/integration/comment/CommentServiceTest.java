package front.meetudy.service.integration.comment;

import front.meetudy.domain.comment.Comment;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.comment.CommentReqDto;
import front.meetudy.dto.request.comment.CommentUpdateReqDto;
import front.meetudy.dto.request.comment.CommentWriteReqDto;
import front.meetudy.dto.response.comment.CommentResDto;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.repository.contact.faq.QuerydslTestConfig;
import front.meetudy.service.comment.CommentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
@AutoConfigureMockMvc(addFilters = false) //  시큐리티 필터 제거
@EnableAspectJAutoProxy(proxyTargetClass = true)
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @PersistenceContext
    private EntityManager em;
    Member member;

    @BeforeEach
    void setUp() {
        member = TestMemberFactory.persistDefaultMember(em);
        em.persist(Comment.createComments(member, "freeboard", 1L, "111", false));
        em.persist(Comment.createComments(member, "freeboard", 1L, "111", false));
        em.persist(Comment.createComments(member, "freeboard", 1L, "111", false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("댓글 조회")
    void commentList() {
        // given / when
        CommentReqDto freeboard1 = new CommentReqDto("freeboard", 1L);
        List<CommentResDto> freeboard = commentService.findCommentList(member, freeboard1);
        //then
        assertThat(freeboard.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("댓글 저장")
    void commentSave() {
        CommentWriteReqDto commentWriteReqDto = new CommentWriteReqDto("freeboard", 1L, "댓글1");
        CommentResDto commentResDto = commentService.commentSave(member, commentWriteReqDto);
        assertThat(commentResDto.getContent()).isEqualTo("댓글1");

    }

    @Test
    @DisplayName("댓글 수정")
    void comment_update() {
        // given
        Comment comments = Comment.createComments(member, "freeboard", 2L, "댓글2", false);
        em.persist(comments);
        CommentUpdateReqDto commentUpdateReqDto = new CommentUpdateReqDto(comments.getId(), comments.getTargetType(), comments.getTargetId(), "댓글-수정");
        // when
        CommentResDto commentResDto = commentService.commentUpdate(member, commentUpdateReqDto);
        // then
        assertThat(commentResDto.getContent()).isEqualTo("댓글-수정");
    }

    @Test
    @DisplayName("댓글 삭제")
    void comment_delete() {
        Comment comments = Comment.createComments(member, "freeboard", 2L, "댓글2", false);
        em.persist(comments);
        commentService.commentDelete(member, comments.getId());

    }

}
