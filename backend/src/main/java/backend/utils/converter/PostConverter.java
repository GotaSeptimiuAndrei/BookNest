package backend.utils.converter;

import backend.dto.request.PostRequest;
import backend.dto.response.PostResponse;
import backend.model.Author;
import backend.model.Community;
import backend.model.Post;
import backend.model.PostLikesId;
import backend.repository.PostLikesRepository;

import java.time.LocalDateTime;
import java.util.Objects;

public class PostConverter {

	public static Post convertToEntity(PostRequest postRequest, String imageUrl, Community community, Author author) {
		Post post = new Post();
		post.setAuthor(author);
		post.setCommunity(community);
		post.setText(postRequest.getText());
		post.setLikeCount(0);
		post.setCommentCount(0);
		post.setDatePosted(LocalDateTime.now());
		post.setImage(Objects.requireNonNullElse(imageUrl, ""));
		return post;
	}

	public static PostResponse convertToDto(Post post) {
		PostResponse postResponse = new PostResponse();

		Author author = post.getAuthor();
		Community community = post.getCommunity();
		postResponse.setPostId(post.getPostId());
		postResponse.setAuthorFullName(author.getFullName());
		postResponse.setCommunityName(community.getName());
		postResponse.setText(post.getText());
		postResponse.setImageUrl(post.getImage());
		postResponse.setLikeCount(post.getLikeCount());
		postResponse.setCommentCount(post.getCommentCount());
		postResponse.setDatePosted(post.getDatePosted());
		postResponse.setLikedByMe(false);

		return postResponse;
	}

	public static PostResponse convertToDtoWithBoolean(Post post, boolean likedByMe) {
		PostResponse postResponse = new PostResponse();

		Author author = post.getAuthor();
		Community community = post.getCommunity();
		postResponse.setPostId(post.getPostId());
		postResponse.setAuthorFullName(author.getFullName());
		postResponse.setCommunityName(community.getName());
		postResponse.setText(post.getText());
		postResponse.setImageUrl(post.getImage());
		postResponse.setLikeCount(post.getLikeCount());
		postResponse.setCommentCount(post.getCommentCount());
		postResponse.setDatePosted(post.getDatePosted());
		postResponse.setLikedByMe(likedByMe);

		return postResponse;
	}

}
