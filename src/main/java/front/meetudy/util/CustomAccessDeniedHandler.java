package front.meetudy.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import front.meetudy.exception.login.LoginErrorCode;
import front.meetudy.util.response.Response;
import front.meetudy.util.response.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Response<String> error = ResponseBuilder.buildError(HttpStatus.UNAUTHORIZED.value(), LoginErrorCode.LG_ANOTHER_ERROR.getMessage());
        String responseBody = om.writeValueAsString(error);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().println(responseBody);
    }
}
