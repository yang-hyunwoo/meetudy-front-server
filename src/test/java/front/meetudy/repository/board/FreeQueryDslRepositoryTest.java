package front.meetudy.repository.board;

import front.meetudy.domain.board.FreeBoard;
import front.meetudy.domain.board.vo.FreeTitle;
import front.meetudy.domain.common.vo.Content;
import front.meetudy.domain.member.Member;
import front.meetudy.dto.request.board.FreePageReqDto;
import front.meetudy.dummy.TestMemberFactory;
import front.meetudy.exception.CustomApiException;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static front.meetudy.constant.error.ErrorEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@Import(QuerydslTestConfig.class)
class FreeQueryDslRepositoryTest {

    @Autowired
    private FreeQueryDslRepository freeQueryDslRepository;

    @Autowired
    private FreeRepository freeRepository;

    @Autowired
    private TestEntityManager em;
    Member member;

    @BeforeEach
    void setUp() {
        member = TestMemberFactory.persistDefaultMember(em);
        Member persist = em.persist(member);
        em.persist(FreeBoard.createFreeBoard(persist, FreeTitle.of("1"), Content.required("1"),false));
        em.persist(FreeBoard.createFreeBoard(persist,FreeTitle.of("2"),Content.required("2"),false));
        em.persist(FreeBoard.createFreeBoard(persist,FreeTitle.of("3"),Content.required("3"),false));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("자유 게시판 페이징 전체 조회")
    void free_paging_all_search() {

        //given
        Pageable pageable = PageRequest.of(0, 10);
        FreePageReqDto freePageReqDto = new FreePageReqDto();

        //when
        Page<FreeBoard> result = freeQueryDslRepository.findFreePage(pageable, freePageReqDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("자유게시판 페이징 검색[타입 전체] 조회 - 데이터 있음")
    void free_paging_all_searchType_all_searchKeyword() {

        //given
        Pageable pageable = PageRequest.of(0, 10);
        FreePageReqDto freePageReqDto = new FreePageReqDto();
        freePageReqDto.setSearchKeyword("1");
        freePageReqDto.setSearchType("ALL");

        //when
        Page<FreeBoard> result = freeQueryDslRepository.findFreePage(pageable, freePageReqDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getContent().get(0).getWriteNickname()).isEqualTo(member.getNickname());
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("자유게시판 페이징 검색[타입 타이틀] 조회 - 데이터 있음")
    void free_paging_all_searchType_title_searchKeyword() {

        //given
        Pageable pageable = PageRequest.of(0, 10);
        FreePageReqDto freePageReqDto = new FreePageReqDto();
        freePageReqDto.setSearchKeyword("1");
        freePageReqDto.setSearchType("TITLE");

        //when
        Page<FreeBoard> result = freeQueryDslRepository.findFreePage(pageable, freePageReqDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getContent().get(0).getWriteNickname()).isEqualTo(member.getNickname());
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("자유게시판 페이징 검색[타입 전체] 조회 - 데이터 없음")
    void free_paging_all_searchType_all_searchKeyword_none() {

        //given
        Pageable pageable = PageRequest.of(0, 10);
        FreePageReqDto freePageReqDto = new FreePageReqDto();
        freePageReqDto.setSearchKeyword("111");
        freePageReqDto.setSearchType("ALL");

        Page<FreeBoard> result = freeQueryDslRepository.findFreePage(pageable, freePageReqDto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    @DisplayName("자유게시판 페이징 검색[타입 제목] 조회 - 데이터 없음")
    void free_paging_all_searchType_title_searchKeyword_none() {

        //given
        Pageable pageable = PageRequest.of(0, 10);
        FreePageReqDto freePageReqDto = new FreePageReqDto();
        freePageReqDto.setSearchKeyword("111");
        freePageReqDto.setSearchType("TITLE");

        //when
        Page<FreeBoard> result = freeQueryDslRepository.findFreePage(pageable, freePageReqDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    @DisplayName("자유게시판 상세 조회 - 성공")
    void free_details_success() {

        //given
        FreeBoard freeBoard1 = FreeBoard.createFreeBoard(member, FreeTitle.of("5"), Content.required("5"), false);
        em.persist(freeBoard1);

        //when
        FreeBoard freeBoard = freeRepository.findByIdAndDeleted(freeBoard1.getId(), false)
                .orElseThrow(() -> new CustomApiException(HttpStatus.BAD_GATEWAY, ERR_012, ERR_012.getValue()));

        //then
        assertThat(freeBoard).isNotNull();
        assertThat(freeBoard.getTitle()).isEqualTo("5");
    }

    @Test
    @DisplayName("자유게시판 상세 조회 - 실패")
    void free_details_fail() {

        //given&when
        CustomApiException exception = assertThrows(CustomApiException.class, () -> {
            freeRepository.findByIdAndDeleted(1L, true)
                    .orElseThrow(() -> new CustomApiException(HttpStatus.NOT_FOUND, ERR_012, ERR_012.getValue()));
        });

        //then
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getErrorEnum()).isEqualTo(ERR_012);
    }

}
