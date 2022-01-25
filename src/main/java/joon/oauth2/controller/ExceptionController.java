package joon.oauth2.controller;


import joon.oauth2.dto.ErrorDto;
import joon.oauth2.exception.KakaoCodeException;
import joon.oauth2.exception.KakaoTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    //400
    @ExceptionHandler({
            KakaoCodeException.class,
            KakaoTokenException.class
    })
    public ResponseEntity<ErrorDto> BadRequestException(final RuntimeException ex) {
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(400).body(
                ErrorDto.builder()
                        .status(400)
                        .message(ex.getMessage())
                        .build()
        );
    }

    //500
    @ExceptionHandler({
            Exception.class
    })
    public ResponseEntity<ErrorDto> HandleAllException(final Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(500).body(
                ErrorDto.builder()
                        .status(500)
                        .message(ex.getMessage())
                        .build()
        );
    }
}
