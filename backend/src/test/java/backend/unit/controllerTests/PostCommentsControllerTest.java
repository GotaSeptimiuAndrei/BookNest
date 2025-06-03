package backend.unit.controllerTests;

import backend.controller.PostCommentsController;
import backend.dto.request.PostCommentRequest;
import backend.dto.response.PostCommentResponse;
import backend.exception.PostCommentsException;
import backend.service.PostCommentsService;
import backend.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostCommentsController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class PostCommentsControllerTest {

	private static final String TOKEN = "Bearer faketoken";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PostCommentsService commentsService;

	@Test
	void getCommentsForPost_success() throws Exception {
		PostCommentResponse a = new PostCommentResponse();
		a.setText("A");
		PostCommentResponse b = new PostCommentResponse();
		b.setText("B");

		when(commentsService.getCommentsForPost(1L)).thenReturn(List.of(a, b));

		mockMvc.perform(get("/api/comments/post/{postId}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.results[0].text").value("A"))
			.andExpect(jsonPath("$.results[1].text").value("B"));
	}

	@Test
	void getCommentsForPost_notFound() throws Exception {
		when(commentsService.getCommentsForPost(999L)).thenThrow(new PostCommentsException("Post not found"));

		mockMvc.perform(get("/api/comments/post/{postId}", 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"));
	}

	@Test
	void createComment_success() throws Exception {
		PostCommentRequest req = new PostCommentRequest();
		req.setPostId(1L);
		req.setText("New");

		PostCommentResponse resp = new PostCommentResponse();
		resp.setCommentId(10L);
		resp.setText("New");

		try (MockedStatic<JwtUtils> jwt = mockStatic(JwtUtils.class)) {
			jwt.when(() -> JwtUtils.extractPrincipalId(TOKEN)).thenReturn(2L);
			jwt.when(() -> JwtUtils.extractRoles(TOKEN)).thenReturn(List.of("ROLE_USER"));

			when(commentsService.createComment(any(PostCommentRequest.class), eq(2L), eq(List.of("ROLE_USER"))))
				.thenReturn(resp);

			mockMvc
				.perform(post("/api/comments").header("Authorization", TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.results.commentId").value(10L));
		}
	}

	@Test
	void replyToComment_success() throws Exception {
		PostCommentRequest req = new PostCommentRequest();
		req.setPostId(1L);
		req.setText("Reply");

		PostCommentResponse resp = new PostCommentResponse();
		resp.setCommentId(20L);
		resp.setText("Reply");

		try (MockedStatic<JwtUtils> jwt = mockStatic(JwtUtils.class)) {
			jwt.when(() -> JwtUtils.extractPrincipalId(TOKEN)).thenReturn(2L);
			jwt.when(() -> JwtUtils.extractRoles(TOKEN)).thenReturn(List.of("ROLE_USER"));

			when(commentsService.replyToComment(eq(10L), any(PostCommentRequest.class), eq(2L),
					eq(List.of("ROLE_USER"))))
				.thenReturn(resp);

			mockMvc
				.perform(post("/api/comments/{parentId}/reply", 10L).header("Authorization", TOKEN)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.results.commentId").value(20L));
		}
	}

	@Test
	void deleteComment_success() throws Exception {
		try (MockedStatic<JwtUtils> jwt = mockStatic(JwtUtils.class)) {
			jwt.when(() -> JwtUtils.extractPrincipalId(TOKEN)).thenReturn(2L);
			jwt.when(() -> JwtUtils.extractRoles(TOKEN)).thenReturn(List.of("ROLE_ADMIN"));

			mockMvc.perform(delete("/api/comments/{id}", 5L).header("Authorization", TOKEN))
				.andExpect(status().isNoContent());

			verify(commentsService).deleteComment(2L, List.of("ROLE_ADMIN"), 5L);
		}
	}

	@Test
	void deleteComment_notFound() throws Exception {
		doThrow(new PostCommentsException("Comment not found")).when(commentsService)
			.deleteComment(anyLong(), anyList(), eq(999L));

		try (MockedStatic<JwtUtils> jwt = mockStatic(JwtUtils.class)) {
			jwt.when(() -> JwtUtils.extractPrincipalId(TOKEN)).thenReturn(2L);
			jwt.when(() -> JwtUtils.extractRoles(TOKEN)).thenReturn(List.of("ROLE_USER"));

			mockMvc.perform(delete("/api/comments/{id}", 999L).header("Authorization", TOKEN))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value("error"));
		}
	}

}