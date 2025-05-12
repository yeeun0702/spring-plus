package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String url = request.getRequestURI();

        // 인증 제외 대상은 바로 통과
        if (url.startsWith("/auth") || url.equals("/health") || url.startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        String bearerJwt = request.getHeader("Authorization");

        if (bearerJwt != null) {
            String jwt = jwtUtil.substringToken(bearerJwt);

            try {
                Claims claims = jwtUtil.extractClaims(jwt);
                if (claims != null) {
                    Long userId = Long.parseLong(claims.getSubject());
                    String email = claims.get("email", String.class);
                    String nickname = claims.get("nickname", String.class);
                    String role = claims.get("userRole", String.class);

                    // Spring Security 권한 부여
                    AuthUser authUser = new AuthUser(userId, email, UserRole.of(role));

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    authUser,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                            );

                    // SecurityContext에 등록
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (SecurityException | MalformedJwtException e) {
                log.error("Invalid JWT signature", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
                return;
            } catch (ExpiredJwtException e) {
                log.error("Expired JWT token", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
                return;
            } catch (UnsupportedJwtException e) {
                log.error("Unsupported JWT token", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
                return;
            } catch (Exception e) {
                log.error("JWT 처리 중 에러", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        } else {
            // 토큰 없는 경우 → 여기서 거를지, SecurityConfig에서 permit 설정에 맡길지 결정
            log.debug("JWT 토큰이 없습니다.");
        }

        filterChain.doFilter(request, response);
    }
}
