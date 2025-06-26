package front.meetudy.controller;

import front.meetudy.constant.error.ErrorEnum;
import front.meetudy.util.MessageUtil;
import front.meetudy.util.response.Response;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static front.meetudy.constant.error.ErrorEnum.*;

/**
 * 매핑되지 않은 경로로 들어온 요청은 자동으로 내부적으로 /error로 포워딩(Forward)
 */
@Deprecated(since = "2025-06-26")
@RestController
@RequiredArgsConstructor
//@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    private final MessageUtil messageUtil;

    @RequestMapping
    public ResponseEntity<Response<String>> handleError(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        String message = messageUtil.getMessage("error.server.ok");
        ErrorEnum errorEnum = ERR_500;

        if (statusCode != null) {
            int code = Integer.parseInt(statusCode.toString());
            if (code == HttpStatus.NOT_FOUND.value()) {
                status = HttpStatus.NOT_FOUND;
                errorEnum = ERR_404;
                message = messageUtil.getMessage("error.not.fount.ok");
            } else if (code == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                status = HttpStatus.METHOD_NOT_ALLOWED;
                errorEnum = ERR_405;
                message = messageUtil.getMessage("error.not.allow.method.ok");
            }
        }
        return Response.error(status, message,errorEnum, null);
    }
}
