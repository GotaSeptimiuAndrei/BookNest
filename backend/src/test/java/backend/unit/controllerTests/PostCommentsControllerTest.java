package backend.unit.controllerTests;

import backend.controller.PostCommentsController;
import backend.dto.request.PostCommentRequest;
import backend.dto.response.PostCommentResponse;
import backend.exception.PostCommentsException;
import backend.service.PostCommentsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class PostCommentsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PostCommentsService postCommentsService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getCommentsForPost_Success() throws Exception {
		PostCommentResponse r1 = new PostCommentResponse();
		r1.setText("First comment");
		PostCommentResponse r2 = new PostCommentResponse();
		r2.setText("Second comment");
		when(postCommentsService.getCommentsForPost(1L)).thenReturn(List.of(r1, r2));

		mockMvc.perform(get("/api/comments/post/{postId}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results[0].text").value("First comment"))
			.andExpect(jsonPath("$.results[1].text").value("Second comment"));
	}

	@Test
	void getCommentsForPost_NotFound() throws Exception {
		when(postCommentsService.getCommentsForPost(999L))
			.thenThrow(new PostCommentsException("Post not found with ID: 999"));

		mockMvc.perform(get("/api/comments/post/{postId}", 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("comment"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("Post not found with ID: 999"));
	}

	@Test
	void createComment_Success() throws Exception {
		PostCommentRequest req = new PostCommentRequest();
		req.setPostId(1L);
		req.setUserId(2L);
		req.setText("New Comment");

		PostCommentResponse resp = new PostCommentResponse();
		resp.setCommentId(10L);
		resp.setText("New Comment");

		when(postCommentsService.createComment(any(PostCommentRequest.class))).thenReturn(resp);

		mockMvc
			.perform(post("/api/comments").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.commentId").value(10L));
	}

	@Test
	void createComment_BindError() throws Exception {
		PostCommentRequest req = new PostCommentRequest();
		req.setUserId(2L);
		req.setText("Missing postId");

		mockMvc
			.perform(post("/api/comments").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("postId"));

		verify(postCommentsService, never()).createComment(any());
	}

	@Test
	void replyToComment_Success() throws Exception {
		PostCommentRequest req = new PostCommentRequest();
		req.setPostId(1L);
		req.setUserId(2L);
		req.setText("Reply");

		PostCommentResponse resp = new PostCommentResponse();
		resp.setCommentId(20L);
		resp.setText("Reply");

		when(postCommentsService.replyToComment(eq(10L), any(PostCommentRequest.class))).thenReturn(resp);

		mockMvc
			.perform(post("/api/comments/{parentId}/reply", 10L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.commentId").value(20L));
	}

	@Test
	void deleteComment_Success() throws Exception {
		mockMvc.perform(delete("/api/comments/{commentId}", 5L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(postCommentsService).deleteComment(5L);
	}

	@Test
	void deleteComment_NotFound() throws Exception {
		doThrow(new PostCommentsException("Comment not found with ID: 999")).when(postCommentsService)
			.deleteComment(999L);

		mockMvc.perform(delete("/api/comments/{commentId}", 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("comment"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("Comment not found with ID: 999"));
	}

}