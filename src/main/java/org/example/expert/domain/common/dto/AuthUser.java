package org.example.expert.domain.common.dto;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@AllArgsConstructor
public class AuthUser implements UserDetails {

    private final Long id;
    private final String email;
    private final String nickName;
    private final UserRole userRole;

    //    public AuthUser(Long id, String email, String nickName, UserRole userRole) {
//        this.id = id;
//        this.email = email;
//        this.nickName = nickName;
//        this.userRole = userRole;
//    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(userRole::name);
    }
    
    @Override
    public String getPassword() {
        return null; // 패스워드 인증 사용하지 않음
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
