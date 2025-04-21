package backend.service;

import backend.dto.request.PostRequest;
import backend.dto.response.PostResponse;
import backend.exception.PostException;
import backend.model.*;
import backend.repository.*;
import backend.utils.converter.PostConverter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.LocalDateTime;
import java.util.List;

import static backend.utils.S3Utils.saveFileToS3Bucket;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;

	private final PostLikesRepository postLikesRepository;

	private final CommunityRepository communityRepository;

	private final AuthorRepository authorRepository;

	private final UserRepository userRepository;

	private final CommunityMembershipService communityMembershipService;

	private final NotificationService notificationService;

	private final S3Client s3Client;

	private final String bucketName;

	public PostResponse createPost(PostRequest postRequest) {
		Community community = communityRepository.findById(postRequest.getCommunityId())
			.orElseThrow(() -> new PostException("Community not found with ID: " + postRequest.getCommunityId()));

		Author author = authorRepository.findById(postRequest.getAuthorId())
			.orElseThrow(() -> new PostException("Author not found with ID: " + postRequest.getAuthorId()));

		String imageUrl = null;
		if (postRequest.getImage() != null && !postRequest.getImage().isEmpty()) {
			imageUrl = saveFileToS3Bucket(s3Client, bucketName, postRequest.getImage());
		}

		Post savedPost = postRepository.save(PostConverter.convertToEntity(postRequest, imageUrl, community, author));

		// Fetch all community members for notifications
		List<User> members = communityMembershipService.getMembersOfCommunity(community.getCommunityId());

		// Send notifications (save in DB and broadcast via WebSocket)
		notificationService.sendNewPostNotifications(community.getCommunityId(), savedPost, members);

		return PostConverter.convertToDto(savedPost);
	}

	public void deletePost(Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException("Post not found with ID: " + postId));

		postRepository.delete(post);
	}

	public PostResponse getPostById(Long postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException("Post not found with ID: " + postId));

		return PostConverter.convertToDto(post);
	}

	public List<PostResponse> getPostsByCommunityId(Long communityId) {
		communityRepository.findById(communityId)
			.orElseThrow(() -> new PostException("Community not found with ID: " + communityId));

		List<Post> posts = postRepository.findAllByCommunityCommunityId(communityId);

		return posts.stream().map(PostConverter::convertToDto).toList();
	}

	public Page<PostResponse> getPostsByCommunityIdPaginated(Long communityId, int page, int size) {
		communityRepository.findById(communityId)
			.orElseThrow(() -> new PostException("Community not found with ID: " + communityId));

		Pageable pageable = PageRequest.of(page, size);
		Page<Post> postPage = postRepository.findAllByCommunityCommunityId(communityId, pageable);
		return postPage.map(PostConverter::convertToDto);
	}

	public void likePost(Long postId, Long userId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException("Post not found with ID: " + postId));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new PostException("User not found with ID: " + userId));

		// Check if the user already liked the post
		PostLikesId postLikesId = new PostLikesId(userId, postId);
		if (postLikesRepository.existsById(postLikesId)) {
			// Already liked, throw an exception
			throw new PostException("User " + userId + " already liked post " + postId);
		}

		PostLikes postLikes = new PostLikes();
		postLikes.setId(postLikesId);
		postLikes.setPost(post);
		postLikes.setUser(user);
		postLikes.setLikedAt(LocalDateTime.now());

		postLikesRepository.save(postLikes);

		post.setLikeCount(post.getLikeCount() + 1);
		postRepository.save(post);
	}

	public void unlikePost(Long postId, Long userId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException("Post not found with ID: " + postId));

		userRepository.findById(userId).orElseThrow(() -> new PostException("User not found with ID: " + userId));

		PostLikesId postLikesId = new PostLikesId(userId, postId);
		PostLikes existingLike = postLikesRepository.findById(postLikesId)
			.orElseThrow(() -> new PostException("User " + userId + " has not liked post " + postId));

		postLikesRepository.delete(existingLike);

		post.setLikeCount(post.getLikeCount() - 1);
		postRepository.save(post);
	}

}
