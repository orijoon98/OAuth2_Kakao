package joon.oauth2.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import joon.oauth2.dto.oauth2.KakaoTokenDto;
import joon.oauth2.entity.User;
import joon.oauth2.enums.Role;
import joon.oauth2.repository.UserRepository;
import joon.oauth2.util.CookieUtil;
import joon.oauth2.util.JwtUtil;
import joon.oauth2.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;
    private final KakaoService kakaoService;
    private final UserRepository userRepository;

    public User oauth2WebLogin(String code) {
        KakaoTokenDto authorization = kakaoService.callTokenApi(code);
        String userInfoFromKakao = kakaoService.getUserInfoByAccessToken(authorization.getAccess_token());

        return loginWithKakaoToken(userInfoFromKakao);
    }

    public User oauth2AppLogin(String token) {
        String userInfoFromKakao = kakaoService.getUserInfoByAccessToken(token);

        return loginWithKakaoToken(userInfoFromKakao);
    }

    public String kakaoToken(String code) {
        KakaoTokenDto authorization = kakaoService.callTokenApi(code);

        return authorization.getAccess_token();
    }

    public Map<String, Cookie> sendCookie(User user) {
        String jwt = jwtUtil.generateToken(user);
        String refreshJwt = jwtUtil.generateRefreshToken(user);
        Cookie accessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, jwt);
        Cookie refreshToken = cookieUtil.createCookie(JwtUtil.REFRESH_TOKEN_NAME, refreshJwt);
        redisUtil.setDataExpire(refreshJwt, user.getEmail(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

        Map<String, Cookie> map = new HashMap<String, Cookie>();
        map.put(JwtUtil.ACCESS_TOKEN_NAME, accessToken);
        map.put(JwtUtil.REFRESH_TOKEN_NAME, refreshToken);

        return map;
    }

    private User loginWithKakaoToken(String userInfoFromKakao) {
        JsonObject userInfo = new Gson().fromJson(userInfoFromKakao, JsonObject.class);
        JsonObject properties = new Gson().fromJson(userInfo.get("properties"), JsonObject.class);
        JsonObject kakaoAccount = new Gson().fromJson(userInfo.get("kakao_account"), JsonObject.class);

        String nickname = properties.get("nickname").toString();
        String email = kakaoAccount.get("email").toString();

        nickname = nickname.substring(1, nickname.length() - 1);
        email = email.substring(1, email.length() - 1);

        String profileImage;
        if(properties.get("profile_image") != null) {
            profileImage = properties.get("profile_image").toString();
            profileImage = profileImage.substring(1, profileImage.length() - 1);
        } else {
            profileImage = null;
        }

        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()) {
            User newUser = User.builder()
                    .name(nickname)
                    .picture(profileImage)
                    .email(email)
                    .role(Role.ROLE_USER)
                    .build();

            return userRepository.save(newUser);
        }

        return user.get();
    }
}
