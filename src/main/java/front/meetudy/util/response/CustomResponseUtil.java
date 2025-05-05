package front.meetudy.util.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import front.meetudy.constant.error.ErrorEnum;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class CustomResponseUtil {

    private static final ObjectMapper om = new ObjectMapper();

    // 성공 응답 - 객체 data를 내려줌
    public static void success(HttpServletResponse response, Object dto, String msg) {
        try {
            Response<Object> responseDto = Response.successRead(msg, dto);
            String responseBody = om.writeValueAsString(responseDto);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().println(responseBody);
        } catch (Exception e) {
            log.error("서버 성공 응답 직렬화 실패", e);
        }
    }

    // 실패 응답 - 에러 메시지와 상태코드를 내려줌
    public static void fail(HttpServletResponse response, String msg, HttpStatus httpStatus, ErrorEnum errorEnum) {
        try {
            Response<String> responseDto = Response.error(httpStatus.value(),errorEnum, msg);
            String responseBody = om.writeValueAsString(responseDto);

            response.setContentType("application/json; charset=utf-8");
            response.setStatus(httpStatus.value());
            response.getWriter().println(responseBody);
        } catch (Exception e) {
            log.error("서버 실패 응답 직렬화 실패", e);
        }
    }
}
