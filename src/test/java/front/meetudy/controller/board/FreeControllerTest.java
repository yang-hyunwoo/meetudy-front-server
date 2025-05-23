package front.meetudy.controller.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import front.meetudy.auth.LoginUser;
import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.board.FreeUpdateReqDto;
import front.meetudy.dto.request.board.FreeWriteReqDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class FreeControllerTest {

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
        em.persist(FreeBoard.createFreeBoard(member,"1","1",false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("자유게시판 목록 조회 - 성공")
    void freeList() throws Exception {
        mockMvc.perform(get("/api/free-board/list")
                        .param("page", "0")
                        .param("size", "10")
                        .param("searchType","ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("1"));
    }

    @Test
    @DisplayName("자유게시판 목록 타입[제목] 조회 - 성공")
    void freeTypeList() throws Exception {
        mockMvc.perform(get("/api/free-board/list")
                        .param("page", "0")
                        .param("size", "10")
                        .param("searchType","TITLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("1"));
    }

    @Test
    @DisplayName("자유게시판 저장 - 성공")
    void freeSaveSucc() throws Exception {
        FreeWriteReqDto reqDto = FreeWriteReqDto.builder()
                .title("11")
                .content("22")
                .build();
        Member savedMember = em.merge(member); // 또는 persist 이후 em.find
        LoginUser loginUser = new LoginUser(savedMember);  // 영속 상태 member 사용

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(post("/api/private/free-board/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("자유 게시판 등록 성공"));

    }

    @Test
    @DisplayName("자유게시판 상세 조회 성공")
    void freeDetail_success() throws Exception {
        // given

        Member savedMember = em.merge(member); // 또는 persist 이후 em.find
        LoginUser loginUser = new LoginUser(savedMember);  // ✅ 영속 상태 member 사용

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when & then
        mockMvc.perform(get("/api/free-board/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("자유 게시판 상세 조회 성공"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("자유게시판 상세 조회 실패 - 게시글 없음")
    void freeDetail_fail() throws Exception {
        // given
        Long freeBoardId = 999L;
        // when & then
        mockMvc.perform(get("/free-board/{id}", freeBoardId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("자유게시판 수정 성공")
    void free_update() throws Exception {
        Member author = em.merge(member);      // 게시글 작성자
        FreeBoard board = FreeBoard.createFreeBoard(author, "title", "content", false);
        em.persist(board);
        em.flush();
        em.clear();

        FreeUpdateReqDto freeUpdateReqDto = new FreeUpdateReqDto(board.getId(), "aaa", "bbb");

        Member savedMember = em.merge(member); // 또는 persist 이후 em.find
        LoginUser loginUser = new LoginUser(savedMember);  // ✅ 영속 상태 member 사용

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(put("/api/private/free-board/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(freeUpdateReqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("자유 게시판 수정 성공"));
    }

    @Test
    @DisplayName("자유게시판 수정 실패 - 권한 실패")
    void free_update_fail_auth() throws Exception{
        Member author = em.merge(member);      // 게시글 작성자
        Member other = em.merge(member2);      // 수정 시도자

        FreeBoard board = FreeBoard.createFreeBoard(author, "title", "content", false);
        em.persist(board);
        em.flush();
        em.clear();

        LoginUser loginUser = new LoginUser(other);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        FreeUpdateReqDto freeUpdateReqDto = new FreeUpdateReqDto(board.getId(), "aaa", "bbb");

        mockMvc.perform(put("/api/private/free-board/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(freeUpdateReqDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errCode").value("ERR_014"));

    }

    @Test
    @DisplayName("자유게시판 삭제 성공")
    void free_delete() throws Exception {
        Member author = em.merge(member);      // 게시글 작성자
        FreeBoard board = FreeBoard.createFreeBoard(author, "title", "content", false);
        em.persist(board);
        em.flush();
        em.clear();
        Member savedMember = em.merge(member); // 또는 persist 이후 em.find
        LoginUser loginUser = new LoginUser(savedMember);  // ✅ 영속 상태 member 사용

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        mockMvc.perform(put("/api/private/free-board/" + board.getId() + "/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("자유 게시판 삭제 성공"));
    }


}