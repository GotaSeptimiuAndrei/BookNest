package backend.service;

import backend.exception.CommunityMembershipException;
import backend.model.Community;
import backend.model.CommunityMembership;
import backend.model.User;
import backend.repository.CommunityMembershipRepository;
import backend.repository.CommunityRepository;
import backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityMembershipService {

	private final CommunityMembershipRepository communityMembershipRepository;

	private final CommunityRepository communityRepository;

	private final UserRepository userRepository;

	public CommunityMembership joinCommunity(Long communityId, Long userId) {
		Community community = communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityMembershipException("Community not found with ID: " + communityId));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CommunityMembershipException("User not found with ID: " + userId));

		communityMembershipRepository.findByCommunityCommunityIdAndUserUserId(communityId, userId).ifPresent(m -> {
			throw new CommunityMembershipException(
					"User " + userId + " is already a member of community " + communityId);
		});

		CommunityMembership membership = new CommunityMembership();
		membership.setCommunity(community);
		membership.setUser(user);

		return communityMembershipRepository.save(membership);
	}

	public void leaveCommunity(Long communityId, Long userId) {
		CommunityMembership membership = communityMembershipRepository
			.findByCommunityCommunityIdAndUserUserId(communityId, userId)
			.orElseThrow(() -> new CommunityMembershipException(
					"No membership found for user " + userId + " in community " + communityId));

		communityMembershipRepository.delete(membership);
	}

	public List<CommunityMembership> getAllMembershipsForUser(Long userId) {
		return communityMembershipRepository.findByUserUserId(userId);
	}

	public List<User> getMembersOfCommunity(Long communityId) {
		List<CommunityMembership> memberships = communityMembershipRepository.findByCommunityCommunityId(communityId);

		return memberships.stream().map(CommunityMembership::getUser).toList();
	}

	public int getNrOfMembersOfCommunity(Long communityId) {
		return communityMembershipRepository.findByCommunityCommunityId(communityId).size();
	}

}
