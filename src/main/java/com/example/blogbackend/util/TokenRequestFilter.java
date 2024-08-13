package com.example.blogbackend.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.blogbackend.entity.model.AntPathRequestMatcher;
import com.example.blogbackend.exception.TokenExpiredException;
import com.example.blogbackend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenRequestFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    private static final
    List<AntPathRequestMatcher> ALLOWED_REQUESTS = Arrays.asList(
            new AntPathRequestMatcher("/user/login", RequestMethod.POST),
            new AntPathRequestMatcher("/user/save", RequestMethod.POST),
            new AntPathRequestMatcher("/board/**", RequestMethod.GET),
            new AntPathRequestMatcher("/total-boards", RequestMethod.GET),
            new AntPathRequestMatcher("/board-count-by-category", RequestMethod.GET),
            new AntPathRequestMatcher("/comment", RequestMethod.POST),
            new AntPathRequestMatcher("/comment/**", RequestMethod.GET)
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestURI = request.getRequestURI();

            boolean isAllowed = ALLOWED_REQUESTS.stream().anyMatch(matcher -> matcher.match(request));

            log.info("Request URI: {}", requestURI);
            if (isAllowed) {
                log.info("실행");
                doFilter(request, response, filterChain);
            } else {
                String token = parseJwt(request);
                if (token == null) {
                    log.info("토큰없음");
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                } else {
                    DecodedJWT tokenInfo = jwtUtil.decodeToken(token);
                    if (tokenInfo != null) {
                        String userId = tokenInfo.getSubject();
                        UserDetails loginUser = userService.loadUserByUsername(userId);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                loginUser, null, loginUser.getAuthorities()
                        );

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        doFilter(request, response, filterChain);
                    } else {
                        log.error("### TokenInfo is Null");
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    }
                }
            }
        } catch (TokenExpiredException e) {
            log.error("### Filter Exception {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
        } catch (Exception e) {
            log.error("### Filter Exception {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }
        return null;
    }
}
