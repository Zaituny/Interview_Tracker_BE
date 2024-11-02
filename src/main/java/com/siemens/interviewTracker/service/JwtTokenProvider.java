package com.siemens.interviewTracker.service;

import java.util.Date;
import org.slf4j.Logger;
import java.io.IOException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import javax.crypto.SecretKey;
import org.slf4j.LoggerFactory;
import jakarta.servlet.FilterChain;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;
import com.siemens.interviewTracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


@Component
public class JwtTokenProvider extends OncePerRequestFilter implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private SecretKey jwtSecret;

    @Value("${app.jwt.access-expiration-ms}")
    private long jwtAccessExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long jwtRefreshExpirationMs;

    public JwtTokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateAccessToken(String email) {
        return generateToken(email, jwtAccessExpirationMs);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, jwtRefreshExpirationMs);
    }

    public String generateToken(String email, long expirationMs) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expirationMs);

            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(jwtSecret)
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating JWT token", e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();
            return !claims.getExpiration().before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.error("JWT token expired: {}", e.getMessage());
        } catch (io.jsonwebtoken.SignatureException e) {
            logger.error("JWT signature is invalid: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (Exception e) {
            logger.error("Failed to parse JWT token", e);
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String token = getTokenFromRequest(request);
        if (token != null && validateToken(token)) {
            String email = getEmailFromToken(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
