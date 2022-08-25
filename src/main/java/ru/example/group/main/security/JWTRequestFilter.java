package ru.example.group.main.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.repository.JwtBlacklistRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    @Value("${config.authorization}")
    private String authHeader;

    private final SocialNetUserDetailsService socialNetUserDetailsService;
    private final JWTUtilService jwtUtilService;
    private final JwtBlacklistRepository jwtBlacklistRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JWTRequestFilter(SocialNetUserDetailsService socialNetUserDetailsService,
                            JWTUtilService jwtUtilService, JwtBlacklistRepository jwtBlacklistRepository,
                            HandlerExceptionResolver handlerExceptionResolver) {
        this.socialNetUserDetailsService = socialNetUserDetailsService;
        this.jwtUtilService = jwtUtilService;
        this.jwtBlacklistRepository = jwtBlacklistRepository;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) {
        String token;
        String username;
        try {
            token = httpServletRequest.getHeader(authHeader);
            username = checkToken(token);
            checkAuthenticationToken(username, token, httpServletRequest);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, e);
        }
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }catch (Exception e){
            handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null, e);
        }
    }

    private void checkAuthenticationToken(String username, String token,
                                          HttpServletRequest httpServletRequest) {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;
            userDetails = socialNetUserDetailsService.loadUserByUsername(username);
            if (jwtUtilService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
    }

    private String checkToken(String token) {
        JwtBlacklistEntity blacklist = this.jwtBlacklistRepository.findJwtBlacklistEntityByJwtBlacklistedToken(
                token);
        if (blacklist == null) {
            return jwtUtilService.extractUsername(token);
        }
        return null;
    }

}
