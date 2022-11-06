package aneesh.dev.userregistrationproject.appuser;

import aneesh.dev.userregistrationproject.registration.token.ConfirmationToken;
import aneesh.dev.userregistrationproject.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private static final String USER_NOT_FOUND_MESSAGE = "User with email %s not found!";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE,email)));
    }

    public String signUpUser(AppUser appUser) {
        Optional<AppUser> optional = appUserRepository.findByEmail(appUser.getEmail());
        AppUser appUser1 = null;
        boolean userExists = true;
        if(optional.isPresent()) {
            appUser1 = optional.get();
        } else {
            userExists = false;
        }
        if((userExists) && (appUser1.getEnabled())) {
            throw new IllegalStateException("User already exists!");
        }
        if(userExists) {
            appUser.setId(appUser1.getId());
        }
        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    public void enableUser(Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() ->
                new IllegalStateException("User with given id not found!"));
        appUser.setEnabled(true);
        appUserRepository.save(appUser);
    }
}
