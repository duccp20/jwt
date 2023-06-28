package com.example.jwt.security.userPrincipal;

import com.example.jwt.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String fullName;
    private String avatar;
    private String phone;
    private Collection<? extends GrantedAuthority> roles;


    public static UserPrincipal build(User user) {
        List<GrantedAuthority> grantedAuthorityList = user.getRoles().stream().map(
                role -> new SimpleGrantedAuthority(role.getName().name())
        ).collect(Collectors.toList());
        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .roles(grantedAuthorityList)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /*
    Tài khoản có không bị hết hạn không?
    */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /*
    Tài khoản có không bị block không?
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /*
    Mã xác nhận có không bị hết hạn hay không?
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    /*Tài khoản được kích hoạt hay chưa?*/
    @Override
    public boolean isEnabled() {
        return true;
    }
}
