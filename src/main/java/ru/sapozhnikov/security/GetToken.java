package ru.sapozhnikov.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.sapozhnikov.dao.UserSecurityDAO;
import ru.sapozhnikov.entity.UserSecurity;

import java.util.Date;
import java.util.Optional;

@Service
public class GetToken {
    private final UserSecurityDAO userSecurityDAO;
    private JwtTokenUtil jwtTokenUtil;
    @Value("${token.username}")
    private String username;
    @Value("${token.password}")
    private String password;
    @Value("${customerNode.url}")
    private StringBuilder url;
    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    public GetToken(UserSecurityDAO userSecurityDAO, JwtTokenUtil jwtTokenUtil) {
        this.userSecurityDAO = userSecurityDAO;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public String getToken() {
        Optional<UserSecurity> userOptional = userSecurityDAO.findByUsername(username);
        if (userOptional.isPresent() &&
                jwtTokenUtil.validateToken(userOptional.get().getToken())) {
            return userOptional.get().getToken();
        }
        return getNewToken();
    }

    private String getNewToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);

        ResponseEntity<String> response = new RestTemplate().postForEntity(url.append("/authenticate").toString(),
                new HttpEntity<>(map, headers), String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        Token token = null;
        try {
            token = objectMapper.readValue(response.getBody(), Token.class);
        } catch (JsonProcessingException e) {
            return null;
        }
        return token.getToken();
    }
}
