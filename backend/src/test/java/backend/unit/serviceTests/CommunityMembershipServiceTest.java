package backend.unit.serviceTests;

import backend.exception.CommunityMembershipException;
import backend.model.Community;
import backend.model.CommunityMembership;
import backend.model.User;
import backend.repository.CommunityMembershipRepository;
import backend.repository.CommunityRepository;
import backend.repository.UserRepository;
import backend.service.CommunityMembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommunityMembershipServiceTest {

	@Mock
	private CommunityMembershipRepository communityMembershipRepository;

	@Mock
	private CommunityRepository communityRepository;

	@Mock
	private UserRepository userRepository;

	private CommunityMembershipService communityMembershipService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		communityMembershipService = new CommunityMembershipService(communityMembershipRepository, communityRepository,
				userRepository);
	}

	@Test
	void testJoinCommunity_Success() {
		Long communityId = 1L;
		Long userId = 10L;

		Community community = new Community();
		community.setCommunityId(communityId);

		User user = new User();
		user.setUserId(userId);

		when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(communityMembershipRepository.findByCommunityCommunityIdAndUserUserId(communityId, userId))
			.thenReturn(Optional.empty());

		CommunityMembership savedMembership = new CommunityMembership();
		savedMembership.setMembershipId(100L);
		savedMembership.setCommunity(community);
		savedMembership.setUser(user);

		when(communityMembershipRepository.save(any(CommunityMembership.class))).thenReturn(savedMembership);

		CommunityMembership result = communityMembershipService.joinCommunity(communityId, userId);

		assertThat(result).isNotNull();
		assertThat(result.getMembershipId()).isEqualTo(100L);
		assertThat(result.getCommunity().getCommunityId()).isEqualTo(communityId);
		assertThat(result.getUser().getUserId()).isEqualTo(userId);

		verify(communityMembershipRepository).save(any(CommunityMembership.class));
	}

	@Test
	void testJoinCommunity_CommunityNotFound() {
		when(communityRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(CommunityMembershipException.class, () -> communityMembershipService.joinCommunity(999L, 10L));

		verify(communityRepository).findById(999L);
		verifyNoInteractions(userRepository);
		verifyNoInteractions(communityMembershipRepository);
	}

	@Test
	void testLeaveCommunity_Success() {
		Long communityId = 1L;
		Long userId = 10L;

		CommunityMembership membership = new CommunityMembership();
		membership.setMembershipId(50L);
		when(communityMembershipRepository.findByCommunityCommunityIdAndUserUserId(communityId, userId))
			.thenReturn(Optional.of(membership));

		communityMembershipService.leaveCommunity(communityId, userId);

		verify(communityMembershipRepository).findByCommunityCommunityIdAndUserUserId(communityId, userId);
		verify(communityMembershipRepository).delete(membership);
	}

	@Test
	void testLeaveCommunity_NoMembershipFound() {
		when(communityMembershipRepository.findByCommunityCommunityIdAndUserUserId(1L, 10L))
			.thenReturn(Optional.empty());

		assertThrows(CommunityMembershipException.class, () -> communityMembershipService.leaveCommunity(1L, 10L));

		verify(communityMembershipRepository).findByCommunityCommunityIdAndUserUserId(1L, 10L);
		verify(communityMembershipRepository, never()).delete(any());
	}

}
