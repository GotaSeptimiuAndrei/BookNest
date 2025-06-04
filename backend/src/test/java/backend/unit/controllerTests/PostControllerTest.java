package backend.unit.controllerTests;

import backend.controller.PostController;
import backend.dto.request.PostRequest;
import backend.dto.response.PostResponse;
import backend.exception.PostException;
import backend.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PostService postService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testGetPostById_Found() throws Exception {
		PostResponse postResponse = new PostResponse();
		postResponse.setText("Hello Post");

		when(postService.getPostById(1L)).thenReturn(postResponse);

		mockMvc.perform(get("/api/posts/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.text").value("Hello Post"));

		verify(postService).getPostById(1L);
	}

	@Test
	void testGetPostById_NotFound() throws Exception {
		when(postService.getPostById(999L)).thenThrow(new PostException("No post found with ID: 999"));

		mockMvc.perform(get("/api/posts/{id}", 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("post"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("No post found with ID: 999"));

		verify(postService).getPostById(999L);
	}

	@Test
	void testGetPostsByCommunityId() throws Exception {
		PostResponse post1 = new PostResponse();
		post1.setText("Post One");
		PostResponse post2 = new PostResponse();
		post2.setText("Post Two");

		when(postService.getPostsByCommunityId(10L)).thenReturn(List.of(post1, post2));

		mockMvc.perform(get("/api/posts/community/{communityId}", 10L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results[0].text").value("Post One"))
			.andExpect(jsonPath("$.results[1].text").value("Post Two"));

		verify(postService).getPostsByCommunityId(10L);
	}

	@Test
	void testCreatePost_Success() throws Exception {
		PostResponse createdPost = new PostResponse();
		createdPost.setText("New Post");

		when(postService.createPost(any(PostRequest.class))).thenReturn(createdPost);

		MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "data".getBytes());

		mockMvc
			.perform(multipart("/api/posts").file(imageFile)
				.param("communityId", "1")
				.param("authorId", "2")
				.param("text", "New Post"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.text").value("New Post"));

		verify(postService).createPost(any(PostRequest.class));
	}

	@Test
	void testCreatePost_BindException() throws Exception {
		mockMvc.perform(multipart("/api/posts").param("authorId", "2").param("text", "Missing communityId"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("communityId"));

		verify(postService, never()).createPost(any());
	}

	@Test
	void testDeletePost_Success() throws Exception {
		mockMvc.perform(delete("/api/posts/{id}", 1L)).andExpect(status().isNoContent());

		verify(postService).deletePost(1L);
	}

	@Test
	void testLikePost_Success() throws Exception {
		mockMvc.perform(post("/api/posts/{id}/like", 5L).param("userId", "10")).andExpect(status().isNoContent());

		verify(postService).likePost(5L, 10L);
	}

	@Test
	void testLikePost_AlreadyLiked() throws Exception {
		doThrow(new PostException("User 10 already liked post 5")).when(postService).likePost(5L, 10L);

		mockMvc.perform(post("/api/posts/{id}/like", 5L).param("userId", "10"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("post"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("User 10 already liked post 5"));

		verify(postService).likePost(5L, 10L);
	}

	@Test
	void testUnlikePost_Success() throws Exception {
		mockMvc.perform(post("/api/posts/{id}/unlike", 5L).param("userId", "10")).andExpect(status().isNoContent());

		verify(postService).unlikePost(5L, 10L);
	}

	@Test
	void testUnlikePost_NotLiked() throws Exception {
		doThrow(new PostException("User 10 has not liked post 5")).when(postService).unlikePost(5L, 10L);

		mockMvc.perform(post("/api/posts/{id}/unlike", 5L).param("userId", "10"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("post"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("User 10 has not liked post 5"));

		verify(postService).unlikePost(5L, 10L);
	}

}
