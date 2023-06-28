package com.example.jwt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpForm {

    private String username;
    private String password;
    private String fullName;
    private String phoneNumber;
    private Set<String> roles;
}
