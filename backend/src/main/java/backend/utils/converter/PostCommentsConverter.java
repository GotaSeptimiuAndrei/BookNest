package backend.utils.converter;

import backend.dto.request.PostCommentRequest;
import backend.dto.response.PostCommentResponse;
import backend.model.PostComments;

import java.time.LocalDateTime;

public class PostCommentsConverter {

	public static PostComments convertToEntity(PostCommentRequest commentRequest) {
		PostComments comment = new PostComments();
		comment.setText(commentRequest.getText());
		comment.setDatePosted(LocalDateTime.now());
		// post, user, parentComment are set in the service, because we need DB lookups
		return comment;
	}

	public static PostCommentResponse convertToDto(PostComments entity) {
		PostCommentResponse response = new PostCommentResponse();
		response.setCommentId(entity.getCommentId());
		response.setPostId(entity.getPost().getPostId());
		response.setUserId(entity.getUser().getUserId());
		response.setUsername(entity.getUser().getUsername());
		response.setText(entity.getText());
		response.setDatePosted(entity.getDatePosted());

		// parent
		if (entity.getParentComment() != null) {
			response.setParentCommentId(entity.getParentComment().getCommentId());
		}
		return response;
	}

}
