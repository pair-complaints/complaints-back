package ru.complaints.pair.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.complaints.pair.config.security.CustomUserDetails;
import ru.complaints.pair.dao.UserCredential;
import ru.complaints.pair.dao.repository.UserCredentialRepository;
import ru.complaints.pair.exception.DuplicatedUsernameException;

@Service
@RequiredArgsConstructor
public class UserCredentialService implements UserDetailsService {
    private final UserCredentialRepository userCredentialRepository;

    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CustomUserDetails create(String username, String password) {
        if (userCredentialRepository.existsByUsername(username)) {
            throw new DuplicatedUsernameException("User with username '%s' already exists".formatted(username));
        }
        UserCredential userCredential = new UserCredential();
        userCredential.setUsername(username);
        userCredential.setPassword(password);
        userCredential = userCredentialRepository.save(userCredential);
        return new CustomUserDetails(userCredential);
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        var userCredential = userCredentialRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        return new CustomUserDetails(userCredential);
    }
}