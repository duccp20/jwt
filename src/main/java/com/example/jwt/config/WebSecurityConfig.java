package com.example.jwt.config;

import com.example.jwt.security.jwt.JwtEntryPoint;
import com.example.jwt.security.jwt.JwtTokenFilter;
import com.example.jwt.security.userPrincipal.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private JwtEntryPoint jwtEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter();
    }

    /*
    mặc định security có 1 UserDetailsService, lúc này ta sẽ để UserDetailService mà ta tự tạo vào
    thay thằng mặc định
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder()); //set encode password
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable() //chặn cors and csrf
                .authorizeRequests()
                .antMatchers("/api/v1/auth/**").permitAll() //cho phép những thằng có format giống như vậy đc truy cập
                .anyRequest().authenticated() //tất cả còn lại phải authen
                .and()
                .exceptionHandling()//trong lúc authen thì có thể sẽ xảy ra loi
                .authenticationEntryPoint(jwtEntryPoint) //và ta sẽ handler bằng thằng này
                .and()
                .sessionManagement()//thằng này để quản lý session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); //set session thành free trạng thái (không lưu trên hệ thống)

        http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
