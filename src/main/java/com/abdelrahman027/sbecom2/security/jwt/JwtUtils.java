package com.abdelrahman027.sbecom2.security.jwt;


import com.abdelrahman027.sbecom2.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    private final ServletRequest httpServletRequest;
    //get jwt form header
    @Value("${spring.app.jwtExpirationMs}")
    Integer jwtExpirationMs;

    @Value("${spring.app.jwtSecret}")
    String jwtSecret;

    @Value("${spring.app.jwtCookie}")
    String jwtCookie;

    public JwtUtils(ServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

//    public String getJwtFromHeader(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            String jwtFromHeader = bearerToken.substring(7);
//            log.warn("Token warn :{}", jwtFromHeader);
//            log.debug("Token debug :{}", jwtFromHeader);
//            return jwtFromHeader;
//        }
//        return null;
//    }

//getting jwt as cookie ALTERNATIVE FROM HEADERS :D
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request,jwtCookie);
        if (cookie != null) return cookie.getValue();
        else return null;
    }

    // generate from username



    public ResponseCookie generateJwtCookie(UserDetailsImpl userDetails) {
        String jwt = generateTokenFromUsername(userDetails.getUsername());
        return ResponseCookie.from(jwtCookie,jwt).path("/api").maxAge(24*60*60).httpOnly(false ).build();
    }

    public ResponseCookie cleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null).path("/api").build();
    }
    public String generateTokenFromUsername(String username) {
//        String username= userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtExpirationMs)))
                .signWith(key())
                .compact();
    }


    //old one Case OF Header JWT
//    public String generateTokenFromUsername(UserDetails userDetails) {
//        String username= userDetails.getUsername();
//        return Jwts.builder()
//                .subject(username)
//                .issuedAt(new Date())
//                .expiration(new Date((new Date().getTime() + jwtExpirationMs)))
//                .signWith(key())
//                .compact();
//    }

    public String getUsernameFromJwtToken(String token){
        return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


    public Boolean validateJwtToken(String jwtToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build()
                    .parseSignedClaims(jwtToken);
            return true;
        }
        catch (MalformedJwtException exception) {
            log.error("invalid jwt exception {}", exception.getMessage());
        }

        catch (ExpiredJwtException exception) {
            log.error("expired token exception {}", exception.getMessage());
        }

        catch (UnsupportedJwtException exception) {
            log.error("unsupported token exception {}", exception.getMessage());
        }

        catch (IllegalArgumentException exception) {
            log.error("jwt claims is empty {}", exception.getMessage());
        }
        return false;
    }


}
