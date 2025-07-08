package front.meetudy.user.repository.comment;

import front.meetudy.domain.comment.Comment;
import front.meetudy.domain.common.vo.Content;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.request.comment.CommentWriteReqDto;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.user.repository.comment.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

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
        member = TestMemberFactory.persistDefaultMember(em);
        em.persist(Comment.createComments(member,"freeboard",1L, Content.required("111"),false));
        em.persist(Comment.createComments(member,"freeboard",1L,Content.required("111"),false));
        em.persist(Comment.createComments(member,"freeboard",1L,Content.required("111"),false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("댓글 조회")
    void comment_search() {

        //when
        List<Comment> freeboard = commentRepository.findCommentList("freeboard");

        //then
        assertThat(freeboard.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("댓글 저장")
    void comment_save() {

        //given
        CommentWriteReqDto commentWriteReqDto = new CommentWriteReqDto("freeboard", 1L, "댓글1");
        Comment entity = commentWriteReqDto.toEntity(member);

        //when
        Comment save = commentRepository.save(entity);

        //then
        assertThat(save.getContent().getValue()).isEqualTo("댓글1");
        assertThat(save.getTargetType()).isEqualTo("freeboard");
    }

}
