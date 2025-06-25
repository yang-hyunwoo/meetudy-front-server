package front.meetudy.util.response;

import front.meetudy.constant.error.ErrorEnum;

public class ResponseBuilder {

    public static <T> Response<T> buildSuccess(String message,
                                               T data
    ) {
        return Response.successRead(message, data);
    }

    public static Response<String> buildError(int httpCode,
                                              String message,
                                              ErrorEnum errorEnum
    ) {
        return Response.error(httpCode,errorEnum, message);
    }

}
