package front.meetudy.util.response;

public class ResponseBuilder {

    public static <T> Response<T> buildSuccess(String message, T data) {
        return Response.successRead(message, data);
    }

    public static Response<String> buildError(int httpCode, String message) {
        return Response.error(httpCode, message);
    }
}
