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
    ERR_012("존재하지 않는 데이터 입니다."),
    ERR_013("계정 정보가 없습니다."),
    ERR_014("정보가 일치하지 않습니다."),
    ERR_015("권한이 없습니다."),
    ERR_016("시작일은 종료일보다 빠를 수 없습니다."),
    ERR_017("시작시간은 종료시간보다 빠를 수 없습니다."),
    ERR_018("DB타입 불일치 오류 입니다."),
    ERR_019("최대 그룹 갯수를 초과 하였습니다."),
    ERR_020("정원을 초과 하였습니다."),
    ERR_021("방장은 그룹을 탈퇴 하실수 없습니다."),
    ERR_022("현재 비밀번호가 일치하지 않습니다."),
    ERR_023("동일한 비밀번호는 사용하실수 없습니다."),
    ERR_404("존재하지 않는 경로입니다."),
    ERR_405("허용되지 않은 HTTP 메서드입니다."),
    ERR_500("서버 오류가 발생했습니다.")
    ;

    private final String value;
}
