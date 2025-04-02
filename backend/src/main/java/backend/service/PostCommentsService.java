package backend.service;

import backend.dto.request.PostCommentRequest;
import backend.dto.response.PostCommentResponse;
import backend.exception.PostCommentsException;
import backend.model.Post;
import backend.model.PostComments;
import backend.model.User;
import backend.repository.PostCommentsRepository;
import backend.repository.PostRepository;
import backend.repository.UserRepository;
import backend.utils.converter.PostCommentsConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostCommentsService {

	private final PostCommentsRepository postCommentsRepository;

	private final PostRepository postRepository;

	private final UserRepository userRepository;

	public PostCommentResponse createComment(PostCommentRequest postCommentRequest) {
		Post post = postRepository.findById(postCommentRequest.getPostId())
			.orElseThrow(() -> new PostCommentsException("Post not found with ID: " + postCommentRequest.getPostId()));

		User user = userRepository.findById(postCommentRequest.getUserId())
			.orElseThrow(() -> new PostCommentsException("User not found with ID: " + postCommentRequest.getUserId()));

		PostComments commentEntity = PostCommentsConverter.convertToEntity(postCommentRequest);
		commentEntity.setPost(post);
		commentEntity.setUser(user);

		if (postCommentRequest.getParentCommentId() != null) {
			PostComments parent = postCommentsRepository.findById(postCommentRequest.getParentCommentId())
				.orElseThrow(() -> new PostCommentsException(
						"Parent comment not found with ID: " + postCommentRequest.getParentCommentId()));

			if (!parent.getPost().getPostId().equals(post.getPostId())) {
				throw new PostCommentsException("Parent comment does not belong to the same post!");
			}
			commentEntity.setParentComment(parent);
		}

		PostComments savedComment = postCommentsRepository.save(commentEntity);
		return PostCommentsConverter.convertToDto(savedComment);
	}

	public void deleteComment(Long commentId) {
		PostComments comment = postCommentsRepository.findById(commentId)
			.orElseThrow(() -> new PostCommentsException("Comment not found with ID: " + commentId));
		postCommentsRepository.delete(comment);
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

	public PostCommentResponse replyToComment(Long parentCommentId, PostCommentRequest commentRequest) {
		commentRequest.setParentCommentId(parentCommentId);
		return createComment(commentRequest);
	}

}
