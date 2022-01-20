package joon.oauth2.controller;

import joon.oauth2.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/oauth2/authorization/kakao")
    public void oauth2AuthorizationKakao(@RequestParam("code") String code) {
        authService.oauth2AuthorizationKakao(code);
    }
}
