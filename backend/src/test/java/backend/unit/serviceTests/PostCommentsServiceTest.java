package backend.unit.serviceTests;

import backend.dto.request.PostCommentRequest;
import backend.dto.response.PostCommentResponse;
import backend.exception.PostCommentsException;
import backend.model.Post;
import backend.model.PostComments;
import backend.model.User;
import backend.repository.PostCommentsRepository;
import backend.repository.PostRepository;
import backend.repository.UserRepository;
import backend.service.PostCommentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PostCommentsServiceTest {

	@Mock
	private PostCommentsRepository postCommentsRepository;

	@Mock
	private PostRepository postRepository;

	@Mock
	private UserRepository userRepository;

	private PostCommentsService postCommentsService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		postCommentsService = new PostCommentsService(postCommentsRepository, postRepository, userRepository);
	}

	@Test
	void createComment_Success() {
		PostCommentRequest req = new PostCommentRequest();
		req.setPostId(1L);
		req.setUserId(2L);
		req.setText("Comment text");

		Post post = new Post();
		post.setPostId(1L);
		User user = new User();
		user.setUserId(2L);

		when(postRepository.findById(1L)).thenReturn(Optional.of(post));
		when(userRepository.findById(2L)).thenReturn(Optional.of(user));
		when(postCommentsRepository.save(any(PostComments.class))).thenAnswer(inv -> {
			PostComments saved = inv.getArgument(0);
			saved.setCommentId(10L);
			return saved;
		});

		PostCommentResponse result = postCommentsService.createComment(req);

		assertThat(result.getCommentId()).isEqualTo(10L);
		assertThat(result.getText()).isEqualTo("Comment text");
		verify(postCommentsRepository).save(any(PostComments.class));
	}

	@Test
	void createComment_PostNotFound() {
		PostCommentRequest req = new PostCommentRequest();
		req.setPostId(999L);
		req.setUserId(2L);
		req.setText("Text");

		when(postRepository.findById(999L)).thenReturn(Optional.empty());
		assertThrows(PostCommentsException.class, () -> postCommentsService.createComment(req));
	}

	@Test
	void deleteComment_adminOrAuthor_success() {
		PostComments comment = new PostComments();
		comment.setCommentId(100L);

		User owner = new User();
		owner.setUserId(42L);
		comment.setUser(owner);

		when(postCommentsRepository.findById(100L)).thenReturn(Optional.of(comment));

		Long principalId = 1L;
		List<String> roles = List.of("ROLE_ADMIN");

		postCommentsService.deleteComment(principalId, roles, 100L);

		verify(postCommentsRepository).delete(comment);
	}

	@Test
	void deleteComment_owner_success() {
		PostComments comment = new PostComments();
		comment.setCommentId(101L);

		User me = new User();
		me.setUserId(2L);
		comment.setUser(me);

		when(postCommentsRepository.findById(101L)).thenReturn(Optional.of(comment));

		postCommentsService.deleteComment(2L, List.of("ROLE_USER"), 101L);

		verify(postCommentsRepository).delete(comment);
	}

	@Test
	void deleteComment_notFound_throws() {
		when(postCommentsRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(PostCommentsException.class,
				() -> postCommentsService.deleteComment(1L, List.of("ROLE_ADMIN"), 999L));
	}

	@Test
	void replyToComment_Success() {
		PostCommentRequest req = new PostCommentRequest();
		req.setPostId(1L);
		req.setUserId(2L);
		req.setText("Reply text");

		Post post = new Post();
		post.setPostId(1L);

		User user = new User();
		user.setUserId(2L);

		PostComments parent = new PostComments();
		parent.setCommentId(10L);
		parent.setPost(post);
		parent.setUser(user);

		when(postRepository.findById(1L)).thenReturn(Optional.of(post));
		when(userRepository.findById(2L)).thenReturn(Optional.of(user));
		when(postCommentsRepository.findById(10L)).thenReturn(Optional.of(parent));
		when(postCommentsRepository.save(any(PostComments.class))).thenAnswer(inv -> {
			PostComments saved = inv.getArgument(0);
			saved.setCommentId(30L);
			return saved;
		});

		PostCommentResponse resp = postCommentsService.replyToComment(10L, req);

		assertThat(resp.getCommentId()).isEqualTo(30L);
		verify(postCommentsRepository).save(any(PostComments.class));
	}

	@Test
	void getChildComments_Success() {
		Post post = new Post();
		post.setPostId(1L);

		User user = new User();
		user.setUserId(2L);

		PostComments parent = new PostComments();
		parent.setCommentId(10L);
		parent.setPost(post);
		parent.setUser(user);

		PostComments child = new PostComments();
		child.setCommentId(11L);
		child.setPost(post);
		child.setUser(user);

		when(postCommentsRepository.findById(10L)).thenReturn(Optional.of(parent));
		when(postCommentsRepository.findByParentCommentCommentId(10L)).thenReturn(List.of(child));

		List<PostCommentResponse> replies = postCommentsService.getChildComments(10L);

		assertThat(replies).hasSize(1);
		assertThat(replies.getFirst().getCommentId()).isEqualTo(11L);
	}

	@Test
	void getCommentsForPost_Success() {
		Post post = new Post();
		post.setPostId(1L);

		User user = new User();
		user.setUserId(2L);

		PostComments c1 = new PostComments();
		c1.setCommentId(10L);
		c1.setPost(post);
		c1.setUser(user);

		PostComments c2 = new PostComments();
		c2.setCommentId(20L);
		c2.setPost(post);
		c2.setUser(user);

		when(postRepository.findById(1L)).thenReturn(Optional.of(post));
		when(postCommentsRepository.findByPostPostId(1L)).thenReturn(List.of(c1, c2));

		List<PostCommentResponse> result = postCommentsService.getCommentsForPost(1L);

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getCommentId()).isEqualTo(10L);
		assertThat(result.get(1).getCommentId()).isEqualTo(20L);
	}

}
