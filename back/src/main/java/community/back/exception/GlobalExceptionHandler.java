package community.back.exception;

import community.back.common.ResponseCode;
import community.back.common.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class})
    public ResponseEntity<ResponseDto> validationExceptionHandler(Exception e) {
        log.warn(e.getMessage(), e);
        ResponseDto responseDto = ResponseDto.fail(ResponseCode.VALIDATION_FAILED);
        return ResponseEntity.status(ResponseCode.VALIDATION_FAILED.getStatus()).body(responseDto);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDto> BusinessExceptionHandler(BusinessException e) {
        log.warn(e.getMessage(), e);
        ResponseDto responseDto = ResponseDto.fail(e);
        return ResponseEntity.status(e.getStatus()).body(responseDto);
    }
}
