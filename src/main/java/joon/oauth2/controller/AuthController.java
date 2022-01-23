package joon.oauth2.controller;

import joon.oauth2.dto.ResponseDto;
import joon.oauth2.entity.User;
import joon.oauth2.service.AuthService;
import joon.oauth2.util.CookieUtil;
import joon.oauth2.util.JwtUtil;
import joon.oauth2.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;
    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity oauth2Login(@RequestParam("code") String code, HttpServletRequest req, HttpServletResponse res) {
        final User user = authService.oauth2Login(code);
        final String token = jwtUtil.generateToken(user);
        final String refreshJwt = jwtUtil.generateRefreshToken(user);
        Cookie accessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, token);
        Cookie refreshToken = cookieUtil.createCookie(JwtUtil.REFRESH_TOKEN_NAME, refreshJwt);
        redisUtil.setDataExpire(refreshJwt, user.getEmail(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);
        res.addCookie(accessToken);
        res.addCookie(refreshToken);

        return ResponseEntity.status(200).body(
                ResponseDto.builder()
                        .status(200)
                        .message("로그인 성공")
                        .data(user)
                        .build()
        );
    }
}
