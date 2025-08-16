package community.back.exception;

import community.back.common.ResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int status;
    private final String message;

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.status = responseCode.getStatus();
        this.message = responseCode.getMessage();
    }
}
