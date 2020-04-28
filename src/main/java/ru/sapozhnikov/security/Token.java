package ru.sapozhnikov.security;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Token {
    private String token;
}
