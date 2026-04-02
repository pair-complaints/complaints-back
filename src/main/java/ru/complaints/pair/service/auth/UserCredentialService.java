package ru.complaints.pair.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.complaints.pair.config.security.CustomUserDetails;
import ru.complaints.pair.dao.UserCredential;
import ru.complaints.pair.dao.repository.UserCredentialRepository;

@Service
@RequiredArgsConstructor
public class UserCredentialService implements UserDetailsService {
    private final UserCredentialRepository userCredentialRepository;

    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    public CustomUserDetails create(String username, String password) {
        //todo(a.steshchenko) подумать над многопоточкой???
        if (userCredentialRepository.existsByUsername(username)) {
            // Заменить на свои исключения
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        UserCredential userCredential = new UserCredential();
        userCredential.setUsername(username);
        userCredential.setPassword(password);
        userCredential = userCredentialRepository.save(userCredential);
        return new CustomUserDetails(userCredential);
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userCredential = userCredentialRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        return new CustomUserDetails(userCredential);
    }
}