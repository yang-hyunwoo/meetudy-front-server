package front.meetudy.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.exception.login.LoginErrorCode;
import front.meetudy.property.JwtProperty;
import front.meetudy.util.response.Response;
import front.meetudy.util.response.ResponseBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static front.meetudy.constant.error.ErrorEnum.*;
import static front.meetudy.constant.security.CookieEnum.*;


public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final JwtProperty jwtProperty;

    public CustomAuthenticationEntryPoint(JwtProperty jwtProperty) {
        this.jwtProperty = jwtProperty;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //인증이 필요 없는 url 에서는 작동 하지 않는다.
        String exception = (String)request.getAttribute("exception");
        if(exception==null) {
            exception = LoginErrorCode.LG_FIRST_LOGIN_ING.getMessage();
        }
        if (jwtProperty.isUseCookie()) {

        } else {
            //쿠키 소멸
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(accessToken.getValue()) || cookie.getName().equals(refreshToken.getValue())||cookie.getName().equals(isAutoLogin.getValue())) {
                        ResponseCookie build = ResponseCookie.from(cookie.getName(), "")
                                .maxAge(0)
                                .path("/")
                                .build();
                        response.addHeader("Set-Cookie", build.toString());
                    }
                }
            }
        }
        responseWrite(response,exception);
    }

    private static void responseWrite(HttpServletResponse response , String msg) throws IOException {
        ObjectMapper om = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Response<String> error = ResponseBuilder.buildError(HttpStatus.UNAUTHORIZED.value(), msg, ERR_004);
        String responseBody = om.writeValueAsString(error);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(responseBody);
    }
}
