package org.example.expert.domain.common.dto;

import lombok.Getter;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser implements UserDetails {

    private final Long id;
    private final String email;
    private final UserRole userRole;

    public AuthUser(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security는 권한 이름 앞에 "ROLE_"이 붙는 걸 기대함
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
    }

    @Override
    public String getPassword() {
        // JWT만 쓰면 비밀번호는 필요 없음 → null 반환
        return null;
    }

    @Override
    public String getUsername() {
        // 고유 식별자로 email 반환 (id를 쓰고 싶으면 id.toString()으로 해도 됨)
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (필요 시 DB 체크로 변경 가능)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부
    }
}
