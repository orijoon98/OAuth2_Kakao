package joon.oauth2.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import joon.oauth2.dto.oauth2.KakaoTokenDto;
import joon.oauth2.entity.User;
import joon.oauth2.enums.Role;
import joon.oauth2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoService kakaoService;
    private final UserRepository userRepository;

    public User oauth2Login(String code) {
        KakaoTokenDto authorization = kakaoService.callTokenApi(code);
        String userInfoFromKakao = kakaoService.getUserInfoByAccessToken(authorization.getAccess_token());

        JsonObject userInfo = new Gson().fromJson(userInfoFromKakao, JsonObject.class);
        JsonObject properties = new Gson().fromJson(userInfo.get("properties"), JsonObject.class);
        JsonObject kakaoAccount = new Gson().fromJson(userInfo.get("kakao_account"), JsonObject.class);

        String nickname = properties.get("nickname").toString();
        String profileImage = properties.get("profile_image").toString();
        String email = kakaoAccount.get("email").toString();

        nickname = nickname.substring(1, nickname.length() - 1);
        profileImage = profileImage.substring(1, profileImage.length() - 1);
        email = email.substring(1, email.length() - 1);

        User user = User.builder()
                .name(nickname)
                .picture(profileImage)
                .email(email)
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        return user;
    }
}
