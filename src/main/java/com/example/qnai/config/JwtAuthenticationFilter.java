package com.example.qnai.config;

import com.example.qnai.repository.BlacklistRepository;
import com.example.qnai.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final CustomUserDetailService userDetailsService;
    private final BlacklistRepository blacklistRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();

        return uri.startsWith("/api/auth/") ||
                uri.startsWith("/swagger-ui/") ||
                uri.startsWith("/v3/api-docs/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String header = request.getHeader("Authorization");

        try {
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                if (tokenProvider.validateToken(token)) {
                    if (blacklistRepository.isBlacklisted(token)) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                "로그아웃된 토큰입니다.");
                        return;
                    }

                    String username = tokenProvider.extractUsername(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch(Exception e){
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
