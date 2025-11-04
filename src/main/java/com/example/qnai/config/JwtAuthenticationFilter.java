package com.example.qnai.config;

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
    private final CustomUserDetailService userDetailsService;
    private final TokenProvider tokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String header = request.getHeader("Authorization");

        // 토큰이 없는 경우 건너뛰기
        if(!header.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7);

        //사용자 정보(이메일) 추출
        final String email = tokenProvider.extractUsername(token);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 이메일이 유효하고, 인증이 중복되지 않았을 경우
        if(email.isEmpty() && authentication == null){

            //토큰 유효기간, 서명 등 검증
            if(tokenProvider.validateToken(token)){

                //사용자 정보 로드
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // 패스워드
                        userDetails.getAuthorities()
                );

                //http 요청 정보를 토큰에 추가
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //토큰을 보안 컨텍스트에 저장(사용자가 인증됨)
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        //다음 필터로 넘김
        filterChain.doFilter(request, response);
    }
}
