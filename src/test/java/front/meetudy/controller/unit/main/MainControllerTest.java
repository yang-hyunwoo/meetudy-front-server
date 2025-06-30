package front.meetudy.controller.unit.main;

import front.meetudy.annotation.SequentialValidator;
import front.meetudy.controller.member.MemberController;
import front.meetudy.exception.CustomExceptionHandler;
import front.meetudy.util.aop.ValidationGroupAspect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ComponentScan(basePackages = "front.meetudy.annotation")
@ActiveProfiles("test")
@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false) //  시큐리티 필터 제거
@Import({
        CustomExceptionHandler.class,
        ValidationGroupAspect.class,       //  AOP Aspect 등록
        SequentialValidator.class          //  내부에서 사용되는 컴포넌트
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;




    @BeforeEach
    void setUp() {

    }



    @Test
    @DisplayName("메인 공지사항 조회")
    void mainNoticeList() {
        // given

        // when

        // then
    }
}
