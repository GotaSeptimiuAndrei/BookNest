package backend.unit.serviceTests;

import backend.dto.request.PostCommentRequest;
import backend.dto.response.PostCommentResponse;
import backend.exception.PostCommentsException;
import backend.model.Author;
import backend.model.Post;
import backend.model.PostComments;
import backend.model.User;
import backend.repository.AuthorRepository;
import backend.repository.PostCommentsRepository;
import backend.repository.PostRepository;
import backend.repository.UserRepository;
import backend.service.PostCommentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCommentsServiceTest {

	@Mock
	private PostCommentsRepository commentsRepo;

	@Mock
	private PostRepository postRepo;

	@Mock
	private UserRepository userRepo;

	@Mock
	private AuthorRepository authorRepo;

	private PostCommentsService service;

	@BeforeEach
	void init() {
		service = new PostCommentsService(commentsRepo, postRepo, userRepo, authorRepo);
	}

	@Test
	void createComment_asUser_success() {
		// given
		PostCommentRequest req = new PostCommentRequest();
		req.setPostId(1L);
		req.setText("Hello");

		Post post = new Post();
		post.setPostId(1L);
		User user = new User();
		user.setUserId(2L);

		when(postRepo.findById(1L)).thenReturn(Optional.of(post));
		when(userRepo.findById(2L)).thenReturn(Optional.of(user));
		when(commentsRepo.save(any(PostComments.class))).thenAnswer(inv -> {
			PostComments pc = inv.getArgument(0);
			pc.setCommentId(10L);
			return pc;
		});

		PostCommentResponse resp = service.createComment(req, 2L, List.of("ROLE_USER"));

		assertThat(resp.getCommentId()).isEqualTo(10L);
		assertThat(resp.getText()).isEqualTo("Hello");
		verify(commentsRepo).save(any(PostComments.class));
	}

	@Test
	void createComment_asAuthor_success() {
		PostCommentRequest req = new PostCommentRequest();
		req.setPostId(1L);
		req.setText("By author");

		Post post = new Post();
		post.setPostId(1L);
		Author author = new Author();
		author.setAuthorId(3L);

		when(postRepo.findById(1L)).thenReturn(Optional.of(post));
		when(authorRepo.findById(3L)).thenReturn(Optional.of(author));
		when(commentsRepo.save(any(PostComments.class))).thenAnswer(inv -> {
			PostComments pc = inv.getArgument(0);
			pc.setCommentId(11L);
			return pc;
		});

		PostCommentResponse resp = service.createComment(req, 3L, List.of("ROLE_AUTHOR"));

		assertThat(resp.getCommentId()).isEqualTo(11L);
		assertThat(resp.getText()).isEqualTo("By author");
	}

	@Test
	void createComment_postNotFound_throws() {
		PostCommentRequest req = new PostCommentRequest();
		req.setPostId(999L);
		req.setText("text");

		when(postRepo.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.createComment(req, 2L, List.of("ROLE_USER")))
			.isInstanceOf(PostCommentsException.class);
	}

	@Test
	void deleteComment_admin_success() {
		PostComments c = new PostComments();
		c.setCommentId(100L);
		when(commentsRepo.findById(100L)).thenReturn(Optional.of(c));

		service.deleteComment(1L, List.of("ROLE_ADMIN"), 100L);

		verify(commentsRepo).delete(c);
	}

	@Test
	void deleteComment_ownerUser_success() {
		User me = new User();
		me.setUserId(2L);
		PostComments c = new PostComments();
		c.setCommentId(101L);
		c.setUser(me);

		when(commentsRepo.findById(101L)).thenReturn(Optional.of(c));

		service.deleteComment(2L, List.of("ROLE_USER"), 101L);

		verify(commentsRepo).delete(c);
	}

	@Test
	void deleteComment_ownerAuthor_success() {
		Author me = new Author();
		me.setAuthorId(3L);
		PostComments c = new PostComments();
		c.setCommentId(102L);
		c.setAuthor(me);

		when(commentsRepo.findById(102L)).thenReturn(Optional.of(c));

		service.deleteComment(3L, List.of("ROLE_AUTHOR"), 102L);

		verify(commentsRepo).delete(c);
	}

	@Test
	void deleteComment_notFound_throws() {
		when(commentsRepo.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.deleteComment(1L, List.of("ROLE_ADMIN"), 999L))
			.isInstanceOf(PostCommentsException.class);
	}

	@Test
	void replyToComment_success() {
		PostCommentRequest req = new PostCommentRequest();
		req.setPostId(1L);
		req.setText("Reply");

		Post post = new Post();
		post.setPostId(1L);
		User user = new User();
		user.setUserId(2L);

		PostComments parent = new PostComments();
		parent.setCommentId(10L);
		parent.setPost(post);
		parent.setUser(user);

		when(postRepo.findById(1L)).thenReturn(Optional.of(post));
		when(userRepo.findById(2L)).thenReturn(Optional.of(user));
		when(commentsRepo.findById(10L)).thenReturn(Optional.of(parent));
		when(commentsRepo.save(any(PostComments.class))).thenAnswer(inv -> {
			PostComments pc = inv.getArgument(0);
			pc.setCommentId(30L);
			return pc;
		});

		PostCommentResponse resp = service.replyToComment(10L, req, 2L, List.of("ROLE_USER"));

		assertThat(resp.getCommentId()).isEqualTo(30L);
	}

	@Test
	void getChildComments_success() {
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

		when(commentsRepo.findById(10L)).thenReturn(Optional.of(parent));
		when(commentsRepo.findByParentCommentCommentId(10L)).thenReturn(List.of(child));

		List<PostCommentResponse> replies = service.getChildComments(10L);

		assertThat(replies).singleElement().matches(r -> Objects.equals(r.getCommentId(), 11L));
	}

	@Test
	void getCommentsForPost_success() {
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

		when(postRepo.findById(1L)).thenReturn(Optional.of(post));
		when(commentsRepo.findByPostPostId(1L)).thenReturn(List.of(c1, c2));

		List<PostCommentResponse> result = service.getCommentsForPost(1L);

		assertThat(result).extracting("commentId").containsExactlyInAnyOrder(10L, 20L);
	}

}