package front.meetudy.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import front.meetudy.auth.LoginUser;
import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.comment.Comment;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.board.FreeUpdateReqDto;
import front.meetudy.dto.request.comment.CommentUpdateReqDto;
import front.meetudy.dto.request.comment.CommentWriteReqDto;
import front.meetudy.dto.response.board.FreeDetailResDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager em;
    Member member;
    Member member2;
    private  final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setUp() {
        member = Member.createMember(null, "test@naver.com", "테스트", "테스트", "19950120", "01011112222", "test", false);
        member2 = Member.createMember(null, "test2@naver.com", "테스트2", "테스트2", "19950120", "01011112222", "test", false);
        em.persist(member);
        em.persist(member2);
        em.persist(Comment.createComments(member, "freeboard", 1L, "test", false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("댓글 조회 - 성공")
    void commentList() throws Exception {
        mockMvc.perform(get("/api/comment/list")
                        .param("targetType","freeboard"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 저장 - 성공")
    void commentSave() throws Exception{
        CommentWriteReqDto commentWriteReqDto = new CommentWriteReqDto("freeboard", 1L, "댓글");
        Member savedMember = em.merge(member); // 또는 persist 이후 em.find
        LoginUser loginUser = new LoginUser(savedMember);  // 영속 상태 member 사용

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        mockMvc.perform(post("/api/private/comment/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentWriteReqDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("댓글 등록 성공"));
    }

    @Test
    @DisplayName("댓글 수정")
    void comment_update() throws Exception{
        Member author = em.merge(member);      // 게시글 작성자
        Comment comments = Comment.createComments(author, "freeboard", 1L, "댓글11", false);
        em.persist(comments);
        em.flush();
        em.clear();

        CommentUpdateReqDto commentUpdateReqDto = new CommentUpdateReqDto(comments.getId(), comments.getTargetType(), comments.getTargetId(), "댓글11수정");
        Member savedMember = em.merge(member); // 또는 persist 이후 em.find
        LoginUser loginUser = new LoginUser(savedMember);  // ✅ 영속 상태 member 사용

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        mockMvc.perform(put("/api/private/comment/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateReqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 수정 성공"));

    }

    @Test
    @DisplayName("댓글 수정 실패 - 권한 실패")
    void commet_update_fail_auth() throws Exception{
        Member author = em.merge(member);      // 게시글 작성자
        Member other = em.merge(member2);      // 수정 시도자

        Comment comments = Comment.createComments(author, "freeboard", 1L, "댓글11", false);
        em.persist(comments);
        em.flush();
        em.clear();

        LoginUser loginUser = new LoginUser(other);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CommentUpdateReqDto commentUpdateReqDto = new CommentUpdateReqDto(comments.getId(), comments.getTargetType(), comments.getTargetId(), "댓글11수정");

        mockMvc.perform(put("/api/private/comment/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateReqDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errCode").value("ERR_014"));
    }


    @Test
    @DisplayName("댓글 삭제 성공")
    void comment_delete() throws Exception {
        Member author = em.merge(member);      // 게시글 작성자
        Comment comments = Comment.createComments(author, "freeboard", 1L, "댓글11", false);
        em.persist(comments);
        em.flush();
        em.clear();
        Member savedMember = em.merge(member); // 또는 persist 이후 em.find
        LoginUser loginUser = new LoginUser(savedMember);  // ✅ 영속 상태 member 사용

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        mockMvc.perform(put("/api/private/comment/" + comments.getId() + "/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 삭제 성공"));
    }
}