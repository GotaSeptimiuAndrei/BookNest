package backend.unit.controllerTests;

import backend.controller.CommunityMembershipController;
import backend.exception.CommunityMembershipException;
import backend.service.CommunityMembershipService;
import backend.dto.CommunityMembershipDTO;
import backend.model.CommunityMembership;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommunityMembershipController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommunityMembershipControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CommunityMembershipService communityMembershipService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testJoinCommunity_Success() throws Exception {
		CommunityMembershipDTO dto = new CommunityMembershipDTO();
		dto.setCommunityId(1L);
		dto.setUserId(10L);

		CommunityMembership membership = new CommunityMembership();
		membership.setMembershipId(100L);

		Mockito.when(communityMembershipService.joinCommunity(dto.getCommunityId(), dto.getUserId()))
			.thenReturn(membership);

		mockMvc
			.perform(post("/api/memberships").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.membershipId").value(100L));

		Mockito.verify(communityMembershipService).joinCommunity(dto.getCommunityId(), dto.getUserId());
	}

	@Test
	void testJoinCommunity_BindException() throws Exception {
		CommunityMembershipDTO dto = new CommunityMembershipDTO();
		dto.setCommunityId(1L);

		mockMvc
			.perform(post("/api/memberships").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("userId"));

		Mockito.verifyNoInteractions(communityMembershipService);
	}

	@Test
	void testLeaveCommunity_Success() throws Exception {
		CommunityMembershipDTO dto = new CommunityMembershipDTO();
		dto.setCommunityId(1L);
		dto.setUserId(10L);

		mockMvc
			.perform(delete("/api/memberships").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		Mockito.verify(communityMembershipService).leaveCommunity(1L, 10L);
	}

	@Test
	void testGetAllMembershipsForUser() throws Exception {
		CommunityMembership membership = new CommunityMembership();
		membership.setMembershipId(50L);
		Mockito.when(communityMembershipService.getAllMembershipsForUser(10L)).thenReturn(List.of(membership));

		mockMvc.perform(get("/api/memberships/user/{userId}", 10L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results[0].membershipId").value(50L));

		Mockito.verify(communityMembershipService).getAllMembershipsForUser(10L);
	}

	@Test
	void testHandleMembershipException() throws Exception {
		CommunityMembershipDTO dto = new CommunityMembershipDTO();
		dto.setCommunityId(1L);
		dto.setUserId(10L);

		Mockito.when(communityMembershipService.joinCommunity(dto.getCommunityId(), dto.getUserId()))
			.thenThrow(new CommunityMembershipException("User 10 is already a member"));

		mockMvc
			.perform(post("/api/memberships").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("membership"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("User 10 is already a member"));

		Mockito.verify(communityMembershipService).joinCommunity(1L, 10L);
	}

}
