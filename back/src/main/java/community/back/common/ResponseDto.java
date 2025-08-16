package community.back.common;

import community.back.exception.BusinessException;
import lombok.Getter;

@Getter
public class ResponseDto {
    private String message;
    private Object data;

    private ResponseDto(ResponseCode responseCode) {
        this.message = responseCode.getMessage();
        this.data = null;
    }

    private ResponseDto(ResponseCode responseCode, Object data) {
        this.message = responseCode.getMessage();
        this.data = data;
    }

    private ResponseDto(BusinessException e) {
        this.message = e.getMessage();
        this.data = null;
    }

    public static ResponseDto fail(BusinessException e) {
        return new ResponseDto(e);
    }

    public static ResponseDto fail(ResponseCode responseCode) {
        return new ResponseDto(responseCode);
    }

    public static ResponseDto success() {
        return new ResponseDto(ResponseCode.SUCCESS);
    }

    public static ResponseDto success(Object object) {
        return new ResponseDto(ResponseCode.SUCCESS, object);
    }
}
