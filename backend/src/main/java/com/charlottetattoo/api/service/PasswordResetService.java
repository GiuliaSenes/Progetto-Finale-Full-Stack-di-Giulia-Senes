package com.charlottetattoo.api.service;

import com.charlottetattoo.api.exception.ApiException;
import com.charlottetattoo.api.model.PasswordResetToken;
import com.charlottetattoo.api.model.User;
import com.charlottetattoo.api.repository.PasswordResetTokenRepository;
import com.charlottetattoo.api.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Recupero password.
 * Non c'è un servizio email collegato: il link viene scritto nella console del backend.
 * In un sito vero qui si invierebbe una email all'utente (per esempio con Spring Mail).
 */
@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int TOKEN_VALID_MINUTES = 60;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final String frontendUrl;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder, @Value("${app.frontend-url}") String frontendUrl) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.frontendUrl = frontendUrl;
    }

    /**
     * Crea il link di reset. Non diciamo mai se l'email esiste o no (il controller risponde sempre
     * allo stesso modo), così nessuno può scoprire chi è registrato.
     */
    @Transactional
    public void requestReset(String rawEmail) {
        User user = userRepository.findByEmail(rawEmail.trim().toLowerCase()).orElse(null);
        if (user == null) {
            return;
        }

        // Un solo link valido per volta
        tokenRepository.deleteByUser(user);

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(hash(token));
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_VALID_MINUTES));
        tokenRepository.save(resetToken);

        log.info("RECUPERO PASSWORD per {} -> apri questo link (valido {} minuti): {}/reset-password?token={}",
                user.getEmail(), TOKEN_VALID_MINUTES, frontendUrl, token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByTokenHash(hash(token))
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> ApiException.badRequest("Il link non è valido o è scaduto: richiedine uno nuovo"));

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Il link si può usare una volta sola
        tokenRepository.deleteByUser(user);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
