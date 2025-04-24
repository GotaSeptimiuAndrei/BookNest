package backend.unit.controllerTests;

import backend.controller.AuthorController;
import backend.dto.response.AuthorResponse;
import backend.exception.AuthorNotFoundException;
import backend.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthorControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@MockitoBean
	AuthorService authorService;

	@Test
	void paginated_success() throws Exception {
		AuthorResponse ar = new AuthorResponse();
		when(authorService.getAllAuthorsPaginated(0, 5))
			.thenReturn(new PageImpl<>(List.of(ar), PageRequest.of(0, 5), 1));

		mockMvc.perform(get("/api/authors/paginated"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.content[0]").exists());
	}

	@Test
	void search_success() throws Exception {
		when(authorService.searchAuthorsByName(eq("j"), eq(0), eq(5)))
			.thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 5), 0));

		mockMvc.perform(get("/api/authors/search").param("query", "j").param("size", "5"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));
	}

	@Test
	void getByFullName_success() throws Exception {
		AuthorResponse ar = new AuthorResponse();
		ar.setFullName("Jane Doe");
		when(authorService.getAuthorByFullName("Jane Doe")).thenReturn(ar);

		mockMvc.perform(get("/api/authors/Jane Doe"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.results.fullName").value("Jane Doe"));
	}

	@Test
	void getByFullName_notFound() throws Exception {
		when(authorService.getAuthorByFullName("Foo")).thenThrow(new AuthorNotFoundException("not found"));

		mockMvc.perform(get("/api/authors/Foo"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("fullName"));
	}

}
