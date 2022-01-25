package joon.oauth2.exception;

public class KakaoCodeException extends RuntimeException {

    private final static String MESSAGE = "카카오 인가코드 오류입니다.";

    public KakaoCodeException() {
        super(MESSAGE);
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
