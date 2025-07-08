package front.meetudy.user.service.integration.board;

import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.board.vo.FreeTitle;
import front.meetudy.domain.common.vo.Content;
import front.meetudy.domain.member.Member;
import front.meetudy.user.dto.PageDto;
import front.meetudy.user.dto.request.board.FreePageReqDto;
import front.meetudy.user.dto.request.board.FreeUpdateReqDto;
import front.meetudy.user.dto.request.board.FreeWriteReqDto;
import front.meetudy.user.dto.response.board.FreeDetailResDto;
import front.meetudy.user.dto.response.board.FreePageResDto;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.exception.CustomApiException;
import front.meetudy.user.repository.contact.faq.QuerydslTestConfig;
import front.meetudy.user.service.board.FreeService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
@AutoConfigureMockMvc(addFilters = false) //  시큐리티 필터 제거
@EnableAspectJAutoProxy(proxyTargetClass = true)
class FreeServiceTest {

    @Autowired
    private FreeService freeService;

    @PersistenceContext
    private EntityManager em;
    Member member;
    Member member2;
    @BeforeEach
    void setUp() {
        member = TestMemberFactory.persistDefaultMember(em);
        member2 = TestMemberFactory.persistDefaultTwoMember(em);
        em.persist(FreeBoard.createFreeBoard(member, FreeTitle.of("1"), Content.required("1"),false));
        em.persist(FreeBoard.createFreeBoard(member,FreeTitle.of("2"),Content.required("2"),false));
        em.persist(FreeBoard.createFreeBoard(member,FreeTitle.of("3"),Content.required("3"),false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("자유게시판 전체 조회")
    void free_all_search() {
        // given
        FreePageReqDto freePageReqDto = new FreePageReqDto();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        PageDto<FreePageResDto> freePage = freeService.findFreePage(pageable, freePageReqDto);

        // then
        assertNotNull(freePage);
        assertEquals(3,freePage.getTotalElements());
    }

    @Test
    @DisplayName("자유게시판 타입-제목 조회")
    void free_all_type_all_search() {
        // given
        FreePageReqDto freePageReqDto = new FreePageReqDto("TITLE","1");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        PageDto<FreePageResDto> freePage = freeService.findFreePage(pageable, freePageReqDto);

        // then
        assertNotNull(freePage);
        assertEquals(1,freePage.getTotalElements());
    }

    @Test
    @DisplayName("자유게시판 저장")
    void free_save() {
        FreeWriteReqDto freeWriteReqDto = new FreeWriteReqDto("111", "2222");
        Long id = freeService.freeSave(member, freeWriteReqDto);
        assertNotNull(id);
        FreeBoard saved = em.find(FreeBoard.class, id);
        assertNotNull(saved);
        assertEquals("111", saved.getTitle() != null ?saved.getTitle().getValue():null);
        assertEquals("2222", saved.getContent() != null ? saved.getContent().getValue():null);
    }

    @Test
    @DisplayName("자유게시판 상세 조회 - 성공")
    void free_detail_success() {
        FreeBoard freeBoard = FreeBoard.createFreeBoard(member, FreeTitle.of("5"), Content.required("5"), false);
        em.persist(freeBoard);
        FreeDetailResDto freeDetailResDto = freeService.freeDetail(freeBoard.getId(), member);
        assertThat(freeDetailResDto).isNotNull();
        assertThat(freeDetailResDto.getTitle()).isEqualTo("5");
    }

    @Test
    @DisplayName("자유게시판 상세 조회 - 실패")
    void free_detail_fail() {
        CustomApiException customApiException = assertThrows(CustomApiException.class, () -> {
            freeService.freeDetail(100L, member);
        });
        assertThat(customApiException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(customApiException.getErrorEnum()).isEqualTo(ERR_012);
    }

    @Test
    @DisplayName("자유게시판 수정")
    void free_update() {
        // given
        FreeBoard freeBoard = FreeBoard.createFreeBoard(member, FreeTitle.of("5"), Content.required("5"), false);
        em.persist(freeBoard);
        FreeUpdateReqDto freeUpdateReqDto = new FreeUpdateReqDto(freeBoard.getId(),"aaa","bbb");
        // when
        Long l = freeService.freeUpdate(member, freeUpdateReqDto);
        FreeDetailResDto freeDetailResDto = freeService.freeDetail(l, null);
        // then
        assertThat("aaa").isEqualTo(freeDetailResDto.getTitle());
    }

    @Test
    @DisplayName("자유게시판 수정 - 실패 -계정 없을 경우")
    void free_update_fail_login() {
        // given
        Member member4 = Member.createMember(null, "test@naver.com", "테스트", "테스트", "19950120", "01011112222", "test", false);

        FreeUpdateReqDto freeUpdateReqDto = new FreeUpdateReqDto(1L,"aaa","bbb");
        CustomApiException customApiException = assertThrows(CustomApiException.class, () -> {
            freeService.freeUpdate(member4,freeUpdateReqDto);
        });
        assertThat(customApiException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(customApiException.getErrorEnum()).isEqualTo(ERR_012);
    }

    @Test
    @DisplayName("자유게시판 수정 - 실패 -수정 권한이 없을 경우")
    void free_update_fail_auth() {
        FreeBoard freeBoard = FreeBoard.createFreeBoard(member2, FreeTitle.of("5"), Content.required("5"), false);
        em.persist(freeBoard);
        FreeUpdateReqDto freeUpdateReqDto = new FreeUpdateReqDto(freeBoard.getId(),"aaa","bbb");
        CustomApiException customApiException = assertThrows(CustomApiException.class, () -> {
            freeService.freeUpdate(member,freeUpdateReqDto);
        });
        assertThat(customApiException.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(customApiException.getErrorEnum()).isEqualTo(ERR_014);
    }

    @Test
    @DisplayName("자유게시판 삭제 후 EXCEPTION 반환")
    void free_delete() {
        // when
        FreeBoard freeBoard = FreeBoard.createFreeBoard(member, FreeTitle.of("5"), Content.required("5"), false);
        em.persist(freeBoard);
        freeService.freeDelete(member, freeBoard.getId());
        assertThrows(CustomApiException.class, () -> {
            freeService.freeDetail(freeBoard.getId(),member);
        });
    }

}
