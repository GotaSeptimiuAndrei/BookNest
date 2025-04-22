package backend.repository;

import backend.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

	boolean existsByEmailAndVerifiedFalse(String email);

	Optional<EmailVerification> findByEmailAndVerificationCodeAndVerifiedFalse(String email, String code);

	void deleteAllByVerifiedFalseAndExpiresAtBefore(LocalDateTime now);

}
