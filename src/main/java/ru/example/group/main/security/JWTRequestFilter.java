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
                                    FilterChain filterChain) throws ServletException, IOException {
        String token;
        String username;
        if (httpServletRequest.getHeader(authHeader) != null) {
            if (!httpServletRequest.getHeader(authHeader).equals("undefined") && !httpServletRequest.getHeader(authHeader).equals("")) {
                token = httpServletRequest.getHeader(authHeader);
                username = checkToken(token, httpServletRequest, httpServletResponse);
                checkAuthenticationToken(username, token, httpServletRequest, httpServletResponse);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void checkAuthenticationToken(String username, String token,
                                          HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException {
        if (username != null){
            UserDetails userDetails;
            userDetails = socialNetUserDetailsService.loadUserByUsername(username);
            if (userDetails != null && jwtUtilService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
                SecurityContextHolder.clearContext();
                handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null,
                        new ServletException("Invalid token."));
                throw new ServletException("Invalid token.");
            }
        } else {
            SecurityContextHolder.getContext().setAuthentication(null);
            SecurityContextHolder.clearContext();
            handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null,
                    new ServletException("Wrong token."));
            throw new ServletException("Wrong token.");
        }
    }

    private String checkToken(String token, HttpServletRequest httpServletRequest,
                              HttpServletResponse httpServletResponse) throws ServletException {
        String username;
        JwtBlacklistEntity blacklist = this.jwtBlacklistRepository.findJwtBlacklistEntityByJwtBlacklistedToken(
                token);
        if (blacklist == null) {
            try {
                username = jwtUtilService.extractUsername(token);
            } catch (Exception e) {
                handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null,
                        new ServletException("Wrong token."));
                return "";
            }

        } else {
            handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null,
                    new ServletException("Expired token."));
            return "";
        }
        return username;
    }

}
