package front.meetudy.constant.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorEnum {
    ERR_001("공백일 수 없습니다."),
    ERR_002("유효성 검사에 실패했습니다."),
    ERR_003("DB에 중복된 값이 있습니다."),
    ERR_004("인증에 실패 하였습니다."),
    ERR_005("암호화에 실패 하였습니다."),
    ERR_006("복호화에 실패 하였습니다."),
    ERR_007("로그인에 실패 하였습니다."),
    ERR_008("삭제된 공지 사항 입니다."),
    ERR_009("Cloudinary HTTP 오류"),
    ERR_010("Cloudinary 연결 실패"),
    ERR_011("Cloudinary 오류"),
    ERR_404("존재하지 않는 경로입니다."),
    ERR_405("허용되지 않은 HTTP 메서드입니다."),
    ERR_500("서버 오류가 발생했습니다.")
    ;

    private final String value;
}
