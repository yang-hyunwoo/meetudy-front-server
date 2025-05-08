package front.meetudy.docs.login;

public class LoginSwaggerJoinProvider {
    public static final String LOGIN_VALIDATION_PW_EMAIL_INVALID = "{\n" +
            "  \"resultCode\": \"ERROR\",\n" +
            "  \"httpCode\": 400,\n" +
            "  \"message\": \"공백일 수 없습니다.\",\n" +
            "  \"errCode\": \"ERR_001\",\n" +
            "  \"errCodeMsg\": \"공백일 수 없습니다.\",\n" +
            "  \"data\": {\n" +
            "    \"field\": \"XXX\",\n" +
            "    \"message\": \"공백일 수 없습니다.\"\n" +
            "  },\n" +
            "  \"timestamp\": \"2025-05-05T12:00:00\"\n" +
            "}";

    public static final String LOGIN_VALIDATION_PW_EMAIL_FAIL = "{\n" +
            "  \"resultCode\": \"ERROR\",\n" +
            "  \"httpCode\": 401,\n" +
            "  \"message\": \"ID 및 비밀번호를 확인해 주세요.\",\n" +
            "  \"errCode\": \"ERR_007\",\n" +
            "  \"errCodeMsg\": \"로그인에 실패 했습니다.\",\n" +
            "  \"data\": null,\n"+
            "  \"timestamp\": \"2025-05-05T12:00:00\"\n" +
            "}";

    public static final String LOGIN_VALIDATION_PW_LOCKED = "{\n" +
            "  \"resultCode\": \"ERROR\",\n" +
            "  \"httpCode\": 401,\n" +
            "  \"message\": \"비밀번호 5회 오류로 인해 계정이 잠겼습니다.\",\n" +
            "  \"errCode\": \"ERR_007\",\n" +
            "  \"errCodeMsg\": \"로그인에 실패 했습니다.\",\n" +
            "  \"data\": null,\n"+
            "  \"timestamp\": \"2025-05-05T12:00:00\"\n" +
            "}";



}
