package front.meetudy.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import front.meetudy.constant.login.LoginErrorCode;
import front.meetudy.util.response.Response;
import front.meetudy.util.response.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

import static front.meetudy.constant.error.ErrorEnum.*;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException
    ) throws IOException {
        ObjectMapper om = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Response<String> error = ResponseBuilder.buildError(HttpStatus.UNAUTHORIZED.value(), LoginErrorCode.LG_ANOTHER_ERROR.getMessage(), ERR_004);
        String responseBody = om.writeValueAsString(error);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().println(responseBody);
    }

}
