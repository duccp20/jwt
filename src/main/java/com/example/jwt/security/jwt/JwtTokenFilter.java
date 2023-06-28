package com.example.jwt.security.jwt;

import com.example.jwt.security.userPrincipal.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


public class JwtTokenFilter extends OncePerRequestFilter {

    public static Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);
    private JwtProvider jwtProvider;
    private UserDetailService userDetailService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            //get token từ request
            String jwtToken = getTokenFromRequest(request);
            // is token valid
            if (jwtToken != null && jwtProvider.validateToken(jwtToken)) {
                // lấy username từ token
                String username = jwtProvider.getUserNameFromToken(jwtToken);
                //fetch data
                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                //userDetails trả về 2 trường hợp: có hoặc k có user
                if (userDetails != null) {
                    //Nếu có thì hệ thống sẽ authen bằng user và password và cho user vào hệ thống
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, //truyền vào bản sao (userPrincipal)
                                    null, //k có mã xác nhận nên để null
                                    userDetails.getAuthorities() //truyền cái quyền
                            );
                    //sau khi đã authen xong thì báo cho hệ thống biết request này đã đc authen
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    //sau khi đã authen thì truyền vào security context để hold lại, miễn token còn hạn thì có thể check thoải mái trong hệ thống
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("failed -> Unauthenticated messages {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    //lấy token
    public String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
