package ru.example.group.main.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.repositories.JwtBlacklistRepository;
import ru.example.group.main.service.JWTUtilService;
import ru.example.group.main.service.SocialNetUserDetailsService;
import ru.example.group.main.service.SocialNetUserRegisterService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    @Value("${header.authorization}")
    private String authHeader;
    private final Logger logger = Logger.getLogger(JWTRequestFilter.class.getName());

    private final SocialNetUserDetailsService socialNetUserDetailsService;
    private final JWTUtilService jwtUtilService;
    private final JwtBlacklistRepository jwtBlacklistRepository;


    public JWTRequestFilter(SocialNetUserDetailsService socialNetUserDetailsService, JWTUtilService jwtUtilService, JwtBlacklistRepository jwtBlacklistRepository) {
        this.socialNetUserDetailsService = socialNetUserDetailsService;
        this.jwtUtilService = jwtUtilService;
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;
        logger.info("origin " + httpServletRequest.getHeader("origin"));
        logger.info("token " + httpServletRequest.getHeader(authHeader));

       if (httpServletRequest.getHeader(authHeader) != null ) {
           if (!httpServletRequest.getHeader(authHeader).equals("undefined")) {
               token = httpServletRequest.getHeader(authHeader);
               username = checkToken(token, httpServletRequest, httpServletResponse);
               checkAuthenticationToken(username, token, httpServletRequest, httpServletResponse);
           }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void checkAuthenticationToken(String username, String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;
            userDetails = (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(username);
            if (jwtUtilService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                logger.info("doFilterInternal - invalid token");
                logoutHeaderProcessing(httpServletRequest, httpServletResponse);
                throw new ServletException("Invalid token.");
            }
        }
    }

    private String checkToken(String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException {
        String username = null;
        JwtBlacklistEntity blacklist = this.jwtBlacklistRepository.findJwtBlacklistEntityByJwtBlacklistedToken(token);
        if (blacklist == null) {
            try {
                username = jwtUtilService.extractUsername(token);
            } catch (Exception e) {
                logoutHeaderProcessing(httpServletRequest, httpServletResponse);
                throw new ServletException("Expired token." + e.getMessage());
            }

        } else {
            logoutHeaderProcessing(httpServletRequest, httpServletResponse);
            throw new ServletException("Expired token.");
        }
        return username;
    }

    public void logoutHeaderProcessing(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String token = null;
        if (response.getHeader(authHeader) != null) {
            token = response.getHeader(authHeader);
            JwtBlacklistEntity jwtBlacklistEntity = new JwtBlacklistEntity();
            jwtBlacklistEntity.setJwtBlacklistedToken(token);
            jwtBlacklistEntity.setRevocationDate(LocalDateTime.now());
            jwtBlacklistRepository.save(jwtBlacklistEntity);
            response.setHeader(authHeader, null);
            HttpSession session = request.getSession();
            SecurityContextHolder.clearContext();
            if (session != null) {
                session.invalidate();
            }
        }
        request.logout();
    }

}
