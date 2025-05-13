package backend.unit.controllerTests;

import backend.controller.CommunityController;
import backend.dto.request.CommunityRequest;
import backend.service.CommunityService;
import backend.model.Community;
import backend.exception.CommunityException;
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

@WebMvcTest(CommunityController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommunityControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CommunityService communityService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testGetCommunityById_Found() throws Exception {
		Community community = new Community();
		community.setCommunityId(1L);
		community.setName("My Community");

		when(communityService.getCommunityById(1L)).thenReturn(community);

		mockMvc.perform(get("/api/communities/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.communityId").value(1L))
			.andExpect(jsonPath("$.results.name").value("My Community"));

		verify(communityService).getCommunityById(1L);
	}

	@Test
	void testGetCommunityById_NotFound() throws Exception {
		when(communityService.getCommunityById(999L))
			.thenThrow(new CommunityException("No community found with ID: 999"));

		mockMvc.perform(get("/api/communities/{id}", 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("community"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("No community found with ID: 999"));

		verify(communityService).getCommunityById(999L);
	}

	@Test
	void testGetCommunityByAuthor_Found() throws Exception {
		Community community = new Community();
		community.setCommunityId(5L);
		community.setName("Author's Fan Club");

		when(communityService.getCommunityByAuthor(10L)).thenReturn(community);

		mockMvc.perform(get("/api/communities/author/{authorId}", 10L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.communityId").value(5L))
			.andExpect(jsonPath("$.results.name").value("Author's Fan Club"));

		verify(communityService).getCommunityByAuthor(10L);
	}

	@Test
	void testGetCommunityByAuthor_NotFound() throws Exception {
		when(communityService.getCommunityByAuthor(999L))
			.thenThrow(new CommunityException("No community found for author ID: 999"));

		mockMvc.perform(get("/api/communities/author/{authorId}", 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("community"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("No community found for author ID: 999"));

		verify(communityService).getCommunityByAuthor(999L);
	}

	@Test
	void testGetAllCommunities() throws Exception {
		Community community1 = new Community();
		community1.setCommunityId(1L);
		community1.setName("Community One");

		Community community2 = new Community();
		community2.setCommunityId(2L);
		community2.setName("Community Two");

		when(communityService.getAllCommunities()).thenReturn(List.of(community1, community2));

		mockMvc.perform(get("/api/communities"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results[0].name").value("Community One"))
			.andExpect(jsonPath("$.results[1].name").value("Community Two"));

		verify(communityService).getAllCommunities();
	}

	private static MockMultipartFile dummyImage() {
		return new MockMultipartFile(
				"photo",
				"cover.jpg",
				"image/jpeg",
				new byte[] {1, 2, 3, 4}
		);
	}

	@Test
	void testCreateCommunity_Success() throws Exception {
		CommunityRequest dto = new CommunityRequest();
		dto.setAuthorId(10L);
		dto.setName("New Community");
		dto.setDescription("A description");

		Community createdCommunity = new Community();
		createdCommunity.setCommunityId(3L);
		createdCommunity.setName("New Community");
		createdCommunity.setDescription("A description");

		when(communityService.createCommunity(any(CommunityRequest.class))).thenReturn(createdCommunity);

		MockMultipartFile image = dummyImage();

		mockMvc.perform(multipart("/api/communities")
						.file(image)
						.param("authorId", "10")
						.param("name", "New Community")
						.param("description", "A description"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.results.communityId").value(3L))
				.andExpect(jsonPath("$.results.name").value("New Community"));
	}

	@Test
	void testCreateCommunity_BindException() throws Exception {
		mockMvc.perform(multipart("/api/communities")
						.param("authorId", "10")
						.param("name", "Invalid Community"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value("error"))
				.andExpect(jsonPath("$.errors[0].field").value("photo")); // missing photo

		verify(communityService, never()).createCommunity(any());
	}

	@Test
	void testUpdateCommunity_Success() throws Exception {
		CommunityRequest dto = new CommunityRequest();
		dto.setAuthorId(10L);
		dto.setName("Updated Community");
		dto.setDescription("Updated Description");

		Community updatedCommunity = new Community();
		updatedCommunity.setCommunityId(1L);
		updatedCommunity.setName("Updated Community");
		updatedCommunity.setDescription("Updated Description");

		when(communityService.updateCommunity(any(CommunityRequest.class))).thenReturn(updatedCommunity);

		MockMultipartFile image = dummyImage();

		mockMvc.perform(multipart("/api/communities")
						.file(image)
						.param("authorId", "10")
						.param("name", "Updated Community")
						.param("description", "Updated Description")
						.with(request -> { request.setMethod("PUT"); return request; }))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.results.communityId").value(1L))
				.andExpect(jsonPath("$.results.name").value("Updated Community"));
	}

	@Test
	void testUpdateCommunity_NotFound() throws Exception {
		when(communityService.updateCommunity(any(CommunityRequest.class)))
			.thenThrow(new CommunityException("No existing community found for author with ID: 999"));

		MockMultipartFile image = dummyImage();

		mockMvc.perform(multipart("/api/communities")
						.file(image)
						.param("authorId", "999")
						.param("name", "No Existing Community")
						.param("description", "Desc")
						.with(request -> { request.setMethod("PUT"); return request; }))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value("error"))
				.andExpect(jsonPath("$.errors[0].field").value("community"))
				.andExpect(jsonPath("$.errors[0].errorMessage")
						.value("No existing community found for author with ID: 999"));
	}

}
