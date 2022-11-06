package aneesh.dev.userregistrationproject.registration.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    private ConfirmationToken getAppUser(String token) {
        return confirmationTokenRepository.findByToken(token).orElseThrow(() ->
                new IllegalStateException("Token not found!"));
    }

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public Long getAppUserId(String token) {
        return getAppUser(token).getAppUser().getId();
    }

    public boolean getAppUserEnabled(String token) {
        return getAppUser(token).getAppUser().getEnabled();
    }
    @Transactional
    public int setConfirmedAt(String token, LocalDateTime confirmedAt) {
        return confirmationTokenRepository.updateConfirmedAt(token, confirmedAt);
    }
}
