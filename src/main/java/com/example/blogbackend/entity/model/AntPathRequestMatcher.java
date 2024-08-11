// TokenRequestFilter에서 토큰이 필요없는 주소인지 판단하기 위한 클래스
package com.example.blogbackend.entity.model;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AntPathRequestMatcher {
    private String pattern;
    private RequestMethod method;

    private final AntPathMatcher matcher = new AntPathMatcher();

    private final List<String> swaggerPatterns =
            Arrays.asList(
                    "/swagger-resources/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/webjars/**",
                    "/h2-console"
            );

    public boolean match(HttpServletRequest request) {
        if (isSwaggerRequest(request.getRequestURI())) {
            return true;
        }
        return matcher.match(pattern, request.getRequestURI())
                && (method == null || RequestMethod.valueOf(request.getMethod()) == method);
    }

    private boolean isSwaggerRequest(String requestURI) {
        return swaggerPatterns.stream().anyMatch(swaggerPattern -> matcher.match(swaggerPattern, requestURI));
    }
}
