package com.example.jwt.security.jwt;

import com.example.jwt.security.userPrincipal.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;
    private static final String SECRET_KEY = "G3B0E9upCRgabPfdRWwqFWIrDX8KCofquIZcZTLGhuNHveEerEBE//Z7PAGGBpx2Gencv+wr/HWSkL4sGDeCyw==";

    public String generateToken(Authentication authentication) {
        //tạo token từ user nên phải gọi thằng userprincial để lấy.
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return Jwts
                .builder()
                //set sub trong payload bằng username
                .setSubject(userPrincipal.getUsername())
                //ngày tạo
                .setIssuedAt(new Date())
                //ngày hết hạn
                .setExpiration(new Date(new Date().getTime() + EXPIRATION_TIME))
                //thêm 1 sign mã hóa
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                //kết hợp các cái trên
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts
                    .parser() //dùng để biến đổi token
                    .setSigningKey(SECRET_KEY) //mở khóa sign key
                    .parseClaimsJws(token); //lấy ra claim
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Failed -> Expired Token Message {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Failed -> Unsupported Token Message {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Failed -> Invalid Format Token Message {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("Failed -> Invalid Signature Token Message {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Failed -> Claims Empty Token Message {}", e.getMessage());
        }

        return false;
    }

    public String getUserNameFromToken(String token) {
        return Jwts
                .parser().parseClaimsJws(token).getBody().getSubject();
    }
}
