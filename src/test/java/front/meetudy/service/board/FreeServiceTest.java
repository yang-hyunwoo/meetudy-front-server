package front.meetudy.service.board;

import front.meetudy.constant.search.SearchType;
import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.PageDto;
import front.meetudy.dto.request.board.FreePageReqDto;
import front.meetudy.dto.response.board.FreePageResDto;
import front.meetudy.repository.contact.faq.QuerydslTestConfig;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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

    @BeforeEach
    void setUp() {
        Member member = Member.createMember(null, "test@naver.com", "테스트", "테스트", "19950120", "01011112222", "test", false);
        em.persist(member);
        em.persist(FreeBoard.createFreeBoard(member,"1","1",false));
        em.persist(FreeBoard.createFreeBoard(member,"2","2",false));
        em.persist(FreeBoard.createFreeBoard(member,"3","3",false));
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
        FreePageReqDto freePageReqDto = new FreePageReqDto(SearchType.TITLE,"1");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        PageDto<FreePageResDto> freePage = freeService.findFreePage(pageable, freePageReqDto);

        // then
        assertNotNull(freePage);
        assertEquals(1,freePage.getTotalElements());
    }

}