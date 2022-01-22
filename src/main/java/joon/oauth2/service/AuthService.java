package joon.oauth2.service;

import joon.oauth2.dto.oauth2.KakaoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoService kakaoService;

    public void oauth2Login(String code) {
        KakaoDto authorization = kakaoService.callTokenApi(code);
        String userInfoFromKakao = kakaoService.getUserInfoByAccessToken(authorization.getAccess_token());
        System.out.println(userInfoFromKakao);
    }
}
