package joon.oauth2.dto.oauth2;

import lombok.Getter;

@Getter
public class KakaoDto {

    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private String expiresIn;
    private String scope;
    private String refreshTokenExpiresIn;
}
