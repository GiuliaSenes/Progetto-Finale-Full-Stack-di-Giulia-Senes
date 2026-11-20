package com.charlottetattoo.api.repository;

import com.charlottetattoo.api.model.PasswordResetToken;
import com.charlottetattoo.api.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserEmail(String email);

    void deleteByUser(User user);
}
