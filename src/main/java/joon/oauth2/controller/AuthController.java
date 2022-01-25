package joon.oauth2.controller;

import joon.oauth2.dto.ResponseDto;
import joon.oauth2.entity.User;
import joon.oauth2.service.AuthService;
import joon.oauth2.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @GetMapping("/web/login")
    public ResponseEntity<ResponseDto> oauth2WebLogin(@RequestParam("code") String code, HttpServletResponse res) {
        User user = authService.oauth2WebLogin(code);
        Map<String, Cookie> map = authService.sendCookie(user);

        res.addCookie(map.get(JwtUtil.ACCESS_TOKEN_NAME));
        res.addCookie(map.get(JwtUtil.REFRESH_TOKEN_NAME));

        log.info("/api/auth/web/login");

        return ResponseEntity.status(200).body(
                ResponseDto.builder()
                        .status(200)
                        .message("로그인 성공")
                        .data(user)
                        .build()
        );
    }

    @GetMapping("/app/login")
    public ResponseEntity<ResponseDto> oauth2AppLogin(@RequestParam("token") String token,  HttpServletResponse res) {
        User user = authService.oauth2AppLogin(token);
        Map<String, Cookie> map = authService.sendCookie(user);

        res.addCookie(map.get(JwtUtil.ACCESS_TOKEN_NAME));
        res.addCookie(map.get(JwtUtil.REFRESH_TOKEN_NAME));

        log.info("/api/auth/app/login");

        return ResponseEntity.status(200).body(
                ResponseDto.builder()
                        .status(200)
                        .message("로그인 성공")
                        .data(user)
                        .build()
        );
    }

    @GetMapping("/kakao/token")
    public ResponseEntity<ResponseDto> kakaoToken(@RequestParam("code") String code) {
        String kakaoToken = authService.kakaoToken(code);

        log.info("/api/auth/kakao/token");

        return ResponseEntity.status(200).body(
                ResponseDto.builder()
                        .status(200)
                        .message("카카오 토큰 발급 성공")
                        .data(kakaoToken)
                        .build()
        );
    }
}
