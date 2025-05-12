package front.meetudy.repository.board;

import front.meetudy.constant.search.SearchType;
import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.board.FreePageReqDto;
import front.meetudy.repository.contact.faq.QuerydslTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
class FreeQueryDslRepositoryTest {

    @Autowired
    private FreeQueryDslRepository freeQueryDslRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        Member member = Member.createMember(null, "test@naver.com", "테스트", "테스트", "19950120", "01011112222", "test", false);
        Member persist = em.persist(member);
        em.persist(FreeBoard.createFreeBoard(persist,"1","1",false));
        em.persist(FreeBoard.createFreeBoard(persist,"2","2",false));
        em.persist(FreeBoard.createFreeBoard(persist,"3","3",false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("자유게시판 페이징 전체 조회")
    void faq_paging_all_search() {
        Pageable pageable = PageRequest.of(0, 10);
        FreePageReqDto freePageReqDto = new FreePageReqDto();

        Page<FreeBoard> result = freeQueryDslRepository.findFreePage(pageable, freePageReqDto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("자유게시판 페이징 검색[타입 전체] 조회 - 데이터 있음")
    void faq_paging_all_searchType_all_searchKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        FreePageReqDto freePageReqDto = new FreePageReqDto();
        freePageReqDto.setSearchKeyword("1");
        freePageReqDto.setSearchType(SearchType.ALL);

        Page<FreeBoard> result = freeQueryDslRepository.findFreePage(pageable, freePageReqDto);

        assertThat(result).isNotNull();
        assertThat(result.getContent().get(0).getWriteNickname()).isEqualTo("테스트");
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("자유게시판 페이징 검색[타입 타이틀] 조회 - 데이터 있음")
    void faq_paging_all_searchType_title_searchKeyword() {
        Pageable pageable = PageRequest.of(0, 10);
        FreePageReqDto freePageReqDto = new FreePageReqDto();
        freePageReqDto.setSearchKeyword("1");
        freePageReqDto.setSearchType(SearchType.TITLE);

        Page<FreeBoard> result = freeQueryDslRepository.findFreePage(pageable, freePageReqDto);

        assertThat(result).isNotNull();
        assertThat(result.getContent().get(0).getWriteNickname()).isEqualTo("테스트");
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("자유게시판 페이징 검색[타입 전체] 조회 - 데이터 없음")
    void faq_paging_all_searchType_all_searchKeyword_none() {
        Pageable pageable = PageRequest.of(0, 10);
        FreePageReqDto freePageReqDto = new FreePageReqDto();
        freePageReqDto.setSearchKeyword("111");
        freePageReqDto.setSearchType(SearchType.ALL);

        Page<FreeBoard> result = freeQueryDslRepository.findFreePage(pageable, freePageReqDto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    @DisplayName("자유게시판 페이징 검색[타입 제목] 조회 - 데이터 없음")
    void faq_paging_all_searchType_title_searchKeyword_none() {
        Pageable pageable = PageRequest.of(0, 10);
        FreePageReqDto freePageReqDto = new FreePageReqDto();
        freePageReqDto.setSearchKeyword("111");
        freePageReqDto.setSearchType(SearchType.TITLE);

        Page<FreeBoard> result = freeQueryDslRepository.findFreePage(pageable, freePageReqDto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(0);
    }

}