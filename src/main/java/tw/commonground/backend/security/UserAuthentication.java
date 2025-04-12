package tw.commonground.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tw.commonground.backend.service.user.entity.FullUserEntity;

import java.util.Collection;
import java.util.List;

public record UserAuthentication(FullUserEntity user) implements Authentication {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return user();
    }

    @Override
    public Object getPrincipal() {
        return user();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return user.getUsername();
    }
}
