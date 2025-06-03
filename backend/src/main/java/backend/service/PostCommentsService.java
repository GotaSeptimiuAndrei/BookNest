package backend.service;

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
import backend.utils.converter.PostCommentsConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostCommentsService {

	private final PostCommentsRepository postCommentsRepository;

	private final PostRepository postRepository;

	private final UserRepository userRepository;

	private final AuthorRepository authorRepository;

	@Transactional
	public PostCommentResponse createComment(PostCommentRequest req, Long principalId, List<String> roles) {

		Post post = postRepository.findById(req.getPostId())
			.orElseThrow(() -> new PostCommentsException("Post not found with ID: " + req.getPostId()));

		PostComments comment = new PostComments();
		comment.setPost(post);
		comment.setText(req.getText());
		comment.setDatePosted(LocalDateTime.now());

		if (roles.contains("ROLE_AUTHOR")) {
			Author author = authorRepository.findById(principalId)
				.orElseThrow(() -> new PostCommentsException("Author not found with ID: " + principalId));
			comment.setAuthor(author);
		}
		else {
			User user = userRepository.findById(principalId)
				.orElseThrow(() -> new PostCommentsException("User not found with ID: " + principalId));
			comment.setUser(user);
		}

		if (req.getParentCommentId() != null) {
			PostComments parent = postCommentsRepository.findById(req.getParentCommentId())
				.orElseThrow(() -> new PostCommentsException(
						"Parent comment not found with ID: " + req.getParentCommentId()));

			if (!Objects.equals(parent.getPost().getPostId(), post.getPostId())) {
				throw new PostCommentsException("Parent comment does not belong to the same post");
			}
			comment.setParentComment(parent);
		}

		PostComments saved = postCommentsRepository.save(comment);
		return PostCommentsConverter.convertToDto(saved);
	}

	@Transactional
	public void deleteComment(Long principalId, List<String> roles, Long commentId) {

		PostComments comment = postCommentsRepository.findById(commentId)
			.orElseThrow(() -> new PostCommentsException("Comment not found with ID: " + commentId));

		boolean owner = comment.getUser() != null && comment.getUser().getUserId().equals(principalId)
				|| comment.getAuthor() != null && comment.getAuthor().getAuthorId().equals(principalId);

		boolean isAdmin = roles.contains("ROLE_ADMIN");

		if (isAdmin || owner) {
			postCommentsRepository.delete(comment);
		}
		else {
			throw new PostCommentsException("You are not allowed to delete this comment");
		}
	}

	public List<PostCommentResponse> getCommentsForPost(Long postId) {
		postRepository.findById(postId)
			.orElseThrow(() -> new PostCommentsException("Post not found with ID: " + postId));

		List<PostComments> allComments = postCommentsRepository.findByPostPostId(postId);
		List<PostCommentResponse> allResponses = allComments.stream().map(PostCommentsConverter::convertToDto).toList();

		Map<Long, PostCommentResponse> responseById = new HashMap<>();
		for (PostCommentResponse response : allResponses) {
			responseById.put(response.getCommentId(), response);
		}

		List<PostCommentResponse> topLevel = new ArrayList<>();
		for (PostCommentResponse response : allResponses) {
			Long parentId = response.getParentCommentId();
			if (parentId == null) {
				topLevel.add(response);
			}
			else {
				PostCommentResponse parentResponse = responseById.get(parentId);
				if (parentResponse != null) {
					parentResponse.getReplies().add(response);
				}
			}
		}
		return topLevel;
	}

	public List<PostCommentResponse> getChildComments(Long parentCommentId) {
		postCommentsRepository.findById(parentCommentId)
			.orElseThrow(() -> new PostCommentsException("Parent comment not found with ID: " + parentCommentId));

		List<PostComments> childComments = postCommentsRepository.findByParentCommentCommentId(parentCommentId);

		return childComments.stream().map(PostCommentsConverter::convertToDto).toList();
	}

	public PostCommentResponse replyToComment(Long parentId, PostCommentRequest req, Long principalId,
			List<String> roles) {
		req.setParentCommentId(parentId);
		return createComment(req, principalId, roles);
	}

}
