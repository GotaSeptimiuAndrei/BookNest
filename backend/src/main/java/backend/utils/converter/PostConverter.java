package backend.utils.converter;

import backend.dto.request.PostRequest;
import backend.dto.response.PostResponse;
import backend.model.Author;
import backend.model.Community;
import backend.model.Post;

import java.time.LocalDateTime;

public class PostConverter {

	public static Post convertToEntity(PostRequest postRequest, String imageUrl, Community community, Author author) {
		Post post = new Post();
		post.setAuthor(author);
		post.setCommunity(community);
		post.setText(postRequest.getText());
		post.setLikeCount(0);
		post.setCommentCount(0);
		post.setDatePosted(LocalDateTime.now());
		if (imageUrl != null) {
			post.setImage(imageUrl);
		}
		return post;
	}

	public static PostResponse convertToDto(Post post) {
		PostResponse postResponse = new PostResponse();

		Author author = post.getAuthor();
		Community community = post.getCommunity();

		postResponse.setAuthorFullName(author.getFullName());
		postResponse.setCommunityName(community.getName());
		postResponse.setText(post.getText());
		postResponse.setImageUrl(post.getImage());
		postResponse.setLikeCount(post.getLikeCount());
		postResponse.setCommentCount(post.getCommentCount());
		postResponse.setDatePosted(post.getDatePosted());

		return postResponse;
	}

}
