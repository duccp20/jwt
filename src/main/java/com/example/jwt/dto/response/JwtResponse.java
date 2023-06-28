package com.example.jwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;







@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class JwtResponse {

    private String status;
    private String token;
    private String type;
    private String fullName;
    private Collection<? extends GrantedAuthority> roles;


}
