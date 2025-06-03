package backend.repository;

import backend.model.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserUserId(Long userId);

	List<Notification> findByUserUserIdAndReadStatusFalse(Long userId);

	Optional<Notification> findByNotificationIdAndUserUserId(Long id, Long userId);

	@Modifying
	@Transactional
	@Query("""
			update Notification n
			set n.readStatus = true
			where n.user.userId = :userId
			  and n.readStatus = false
			""")
	void markAllRead(@Param("userId") Long userId);

	void deleteAllByReadStatusIsTrue();

}
