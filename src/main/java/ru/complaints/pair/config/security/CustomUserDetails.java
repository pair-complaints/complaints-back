package ru.complaints.pair.config.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.complaints.pair.dao.UserCredential;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {
    private final UUID userId;
    private final String username;
    private final String password;

    public  CustomUserDetails(UserCredential userCredential) {
        userId = userCredential.getId();
        username = userCredential.getUsername();
        password = userCredential.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
