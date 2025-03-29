package backend.repository;

import backend.model.CommunityMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityMembershipRepository extends JpaRepository<CommunityMembership, Long> {

	Optional<CommunityMembership> findByCommunityCommunityIdAndUserUserId(Long communityId, Long userId);

	List<CommunityMembership> findByUserUserId(Long userId);

	List<CommunityMembership> findByCommunityCommunityId(Long communityId);

}
