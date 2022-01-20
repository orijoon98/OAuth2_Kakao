package joon.oauth2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import joon.oauth2.dto.oauth2.KakaoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kakao.client-id}")
    private final String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private final String kakaoRedirectUrl;

    @Value("${kakao.authorization-grant-type}")
    private final String kakaoGrantType;

    @Value("${kakao.client-secret}")
    private final String kakaoClientSecret;

    @Value("${kakao.token-uri}")
    private final String kakaoTokenUri;

    @Value("${kakao.user-info-uri}")
    private final String kakaoUserInfoUri;

    public KakaoDto callTokenApi(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", kakaoGrantType);
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUrl);
        params.add("code", code);
        params.add("client_secret", kakaoClientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoTokenUri, request, String.class);

            KakaoDto authorization = objectMapper.readValue(response.getBody(), KakaoDto.class);

            return authorization;
        } catch (RestClientException | JsonProcessingException ex) {
            ex.printStackTrace();

            throw new Error();
        }
    }

    public String getUserInfoByAccessToken(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoUserInfoUri, request, String.class);

            return response.getBody();
        }catch (RestClientException ex) {
            ex.printStackTrace();

            throw new Error();
        }
    }
}
