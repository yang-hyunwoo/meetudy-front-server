package front.meetudy.constant.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TokenErrorCodeEnum {

    SC_REFRESH_TOKEN_MISSING("SC_ERR400","리프레시 토큰이 없습니다"),
    SC_REFRESH_TOKEN_EXPIRED("SC_ERR401","리프레시 토큰이 만료되었습니다"),
    SC_ACCESS_TOKEN_EXPIRED("SC_ERR402","액세스 토큰이 만료되었습니다"),
    SC_ACCESS_TOKEN_INVALID("SC_ERR403","액세스 토큰 검증에 실패 하였습니다."),
    SC_REFRESH_TOKEN_INVALID("SC_ERR404","리프레시 토큰 검증에 실패 하였습니다."),
    SC_INVALID_TOKEN_FORMAT("SC_ERR405","잘못된 토큰 형식 입니다."),
    SC_TOKEN_DECODE_ERROR("SC_ERR406","decode 안되는 토큰 입니다."),
    SC_ALGORITHM_ERROR("SC_ERR400","알고리즘 관련 오류 입니다.");
    ;



    private final String code;
    private final String value;
}
