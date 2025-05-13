package backend.integration;

import backend.dto.request.CommunityRequest;
import backend.dto.request.PostRequest;
import backend.model.Author;
import backend.model.User;
import backend.repository.*;
import backend.service.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.*;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommunityPostWorkflowIT {

	@TestConfiguration
	static class BucketStub {

		@Bean("bucketName")
		String bucketName() {
			return "test-bucket";
		}

	}

	@MockitoBean
	S3Client s3Client;

	@MockitoBean
	SimpMessagingTemplate wsTemplate;

	@Autowired
	CommunityService communityService;

	@Autowired
	CommunityMembershipService membershipService;

	@Autowired
	PostService postService;

	@Autowired
	AuthorRepository authorRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	PostRepository postRepo;

	@Autowired
	NotificationRepository notificationRepo;

	Author author;

	User andrei;

	@BeforeEach
	void seed() {
		author = new Author();
		author.setEmail("author@email.com");
		author.setFullName("John Writer");
		author.setPassword("password");
		authorRepo.save(author);

		andrei = new User();
		andrei.setUsername("andrei");
		andrei.setEmail("andrei@email.com");
		andrei.setPassword("password");
		userRepo.save(andrei);

		when(s3Client.putObject((PutObjectRequest) any(), any(RequestBody.class))).thenReturn(null);
	}

	@Test
	void full_community_post_flow() {
		MockMultipartFile banner = new MockMultipartFile("photo", "banner.png", "image/png", new byte[] { 1 });

		CommunityRequest communityRequest = new CommunityRequest(author.getAuthorId(), "Sci‑Fi Fans",
				"Discuss my coming books", banner);

		var community = communityService.createCommunity(communityRequest);

		membershipService.joinCommunity(community.getCommunityId(), andrei.getUserId());
		assertThat(membershipService.getMembersOfCommunity(community.getCommunityId())).extracting(User::getUserId)
			.containsExactly(andrei.getUserId());

		MockMultipartFile img = new MockMultipartFile("image", "post.jpg", "image/jpeg", new byte[] { 1 });
		PostRequest postReq = new PostRequest(community.getCommunityId(), author.getAuthorId(),
				"Cover reveal next week", img);

		postService.createPost(postReq);

		assertThat(postRepo.findAll()).hasSize(1);

		assertThat(notificationRepo.findAll()).hasSize(1);

		// Capture WebSocket destination
		ArgumentCaptor<String> dest = ArgumentCaptor.forClass(String.class);
		verify(wsTemplate).convertAndSend(dest.capture(), Optional.ofNullable(any()));
		assertThat(dest.getValue())
			.isEqualTo("/topic/communities/" + community.getCommunityId() + "/user/" + andrei.getUserId());
	}

}
