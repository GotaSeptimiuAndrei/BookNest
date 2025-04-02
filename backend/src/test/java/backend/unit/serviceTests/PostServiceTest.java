package backend.unit.serviceTests;

import backend.dto.request.PostRequest;
import backend.dto.response.PostResponse;
import backend.exception.PostException;
import backend.model.*;
import backend.repository.*;
import backend.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PostServiceTest {

	@Mock
	private PostRepository postRepository;

	@Mock
	private PostLikesRepository postLikesRepository;

	@Mock
	private CommunityRepository communityRepository;

	@Mock
	private AuthorRepository authorRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private S3Client s3Client;

	private PostService postService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		String bucketName = "bucket-posts";
		postService = new PostService(postRepository, postLikesRepository, communityRepository, authorRepository,
				userRepository, s3Client, bucketName);
	}

	@Test
	void testCreatePost_Success() {
		Community community = new Community();
		community.setCommunityId(1L);

		Author author = new Author();
		author.setAuthorId(10L);

		MockMultipartFile imageFile = new MockMultipartFile("image", "test-image.jpg", "image/jpeg",
				"TestImageContent".getBytes());

		PostRequest request = new PostRequest();
		request.setCommunityId(community.getCommunityId());
		request.setAuthorId(author.getAuthorId());
		request.setText("A new post");
		request.setImage(imageFile);

		when(communityRepository.findById(community.getCommunityId())).thenReturn(Optional.of(community));
		when(authorRepository.findById(author.getAuthorId())).thenReturn(Optional.of(author));
		when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
			Post savedPost = invocation.getArgument(0);
			savedPost.setPostId(100L);
			return savedPost;
		});

		PostResponse response = postService.createPost(request);

		assertThat(response).isNotNull();
		assertThat(response.getText()).isEqualTo("A new post");
		verify(communityRepository).findById(1L);
		verify(authorRepository).findById(10L);
		verify(postRepository).save(any(Post.class));
	}

	@Test
	void testCreatePost_CommunityNotFound() {
		PostRequest request = new PostRequest();
		request.setCommunityId(999L);
		request.setAuthorId(10L);
		request.setText("Missing community");

		when(communityRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(PostException.class, () -> postService.createPost(request));

		verify(communityRepository).findById(999L);
		verify(postRepository, never()).save(any(Post.class));
	}

	@Test
	void testCreatePost_AuthorNotFound() {
		PostRequest request = new PostRequest();
		request.setCommunityId(1L);
		request.setAuthorId(999L);
		request.setText("Missing author");

		when(communityRepository.findById(1L)).thenReturn(Optional.of(new Community()));
		when(authorRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(PostException.class, () -> postService.createPost(request));

		verify(communityRepository).findById(1L);
		verify(authorRepository).findById(999L);
		verify(postRepository, never()).save(any(Post.class));
	}

	@Test
	void testGetPostById_Success() {
		Community community = new Community();
		community.setCommunityId(1L);
		community.setName("test-community");

		Author author = new Author();
		author.setAuthorId(10L);
		author.setFullName("test-author");

		Post post = new Post();
		post.setPostId(123L);
		post.setText("Hello World");
		post.setAuthor(author);
		post.setCommunity(community);
		post.setDatePosted(LocalDateTime.now());
		post.setCommentCount(2);
		post.setLikeCount(3);
		post.setImage("image.jpg");
		when(postRepository.findById(123L)).thenReturn(Optional.of(post));

		PostResponse response = postService.getPostById(123L);

		assertThat(response).isNotNull();
		assertThat(response.getText()).isEqualTo("Hello World");
		verify(postRepository).findById(123L);
	}

	@Test
	void testGetPostById_NotFound() {
		when(postRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(PostException.class, () -> postService.getPostById(999L));
		verify(postRepository).findById(999L);
	}

	@Test
	void testDeletePost_Success() {
		Post post = new Post();
		post.setPostId(100L);

		when(postRepository.findById(100L)).thenReturn(Optional.of(post));

		postService.deletePost(100L);

		verify(postRepository).findById(100L);
		verify(postRepository).delete(post);
	}

	@Test
	void testDeletePost_NotFound() {
		when(postRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(PostException.class, () -> postService.deletePost(999L));
		verify(postRepository).findById(999L);
		verify(postRepository, never()).delete(any());
	}

	@Test
	void testLikePost_Success() {
		Post post = new Post();
		post.setPostId(100L);
		post.setLikeCount(0);

		User user = new User();
		user.setUserId(50L);

		when(postRepository.findById(100L)).thenReturn(Optional.of(post));
		when(userRepository.findById(50L)).thenReturn(Optional.of(user));
		when(postLikesRepository.existsById(any())).thenReturn(false);

		postService.likePost(100L, 50L);

		assertThat(post.getLikeCount()).isEqualTo(1);
		verify(postRepository).save(post);
		verify(postLikesRepository).save(any(PostLikes.class));
	}

	@Test
	void testLikePost_AlreadyLiked() {
		when(postRepository.findById(100L)).thenReturn(Optional.of(new Post()));
		when(userRepository.findById(50L)).thenReturn(Optional.of(new User()));
		when(postLikesRepository.existsById(any())).thenReturn(true);

		assertThrows(PostException.class, () -> postService.likePost(100L, 50L));

		verify(postLikesRepository, never()).save(any(PostLikes.class));
		verify(postRepository, never()).save(any(Post.class));
	}

	@Test
	void testUnlikePost_Success() {
		Post post = new Post();
		post.setPostId(100L);
		post.setLikeCount(3);

		PostLikes postLikes = new PostLikes();
		postLikes.setId(new PostLikesId(50L, 100L));

		when(postRepository.findById(100L)).thenReturn(Optional.of(post));
		when(userRepository.findById(50L)).thenReturn(Optional.of(new User()));
		when(postLikesRepository.findById(any())).thenReturn(Optional.of(postLikes));

		postService.unlikePost(100L, 50L);

		assertThat(post.getLikeCount()).isEqualTo(2);
		verify(postLikesRepository).delete(postLikes);
		verify(postRepository).save(post);
	}

}
