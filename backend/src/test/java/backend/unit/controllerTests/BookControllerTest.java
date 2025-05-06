package backend.unit.controllerTests;

import backend.controller.BookController;
import backend.dto.request.BookRequest;
import backend.dto.response.BookResponse;
import backend.exception.BookNotFoundException;
import backend.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BookService bookService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testGetAllBooks() throws Exception {
		BookResponse book1 = new BookResponse();
		book1.setBookId(1L);
		book1.setTitle("Title 1");

		BookResponse book2 = new BookResponse();
		book2.setBookId(2L);
		book2.setTitle("Title 2");

		when(bookService.getAllBooks()).thenReturn(List.of(book1, book2));

		mockMvc.perform(get("/api/books"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results[0].title").value("Title 1"))
			.andExpect(jsonPath("$.results[1].title").value("Title 2"));

		verify(bookService).getAllBooks();
	}

	@Test
	void testGetBookById_Found() throws Exception {
		BookResponse bookRequest = new BookResponse();
		bookRequest.setBookId(1L);
		bookRequest.setTitle("Some Book Title");

		when(bookService.getBookById(1L)).thenReturn(bookRequest);

		mockMvc.perform(get("/api/books/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.title").value("Some Book Title"));

		verify(bookService).getBookById(1L);
	}

	@Test
	void testGetBookById_NotFound() throws Exception {
		when(bookService.getBookById(999L)).thenThrow(new BookNotFoundException("Book not found with id: 999"));

		mockMvc.perform(get("/api/books/{id}", 999))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("id"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("Book not found with id: 999"));
	}

	@Test
	void testCreateBook() throws Exception {
		MockMultipartFile mockFile = new MockMultipartFile("image", "test-image.jpg", "image/jpeg",
				"DummyImageContent".getBytes());

		BookResponse responseDto = new BookResponse();
		responseDto.setBookId(1L);
		responseDto.setTitle("New Book");
		responseDto.setAuthor("John Doe");
		responseDto.setDescription("A brand new book");
		responseDto.setCopies(10);
		responseDto.setCategory("Fiction");
		responseDto.setImage("some-image-url");

		when(bookService.saveBook(any(BookRequest.class))).thenReturn(responseDto);

		mockMvc.perform(multipart("/api/books")

			.file(mockFile)
			.param("title", "New Book")
			.param("author", "John Doe")
			.param("description", "A brand new book")
			.param("copies", "10")
			.param("copiesAvailable", "10")
			.param("category", "Fiction")
			.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.bookId").value(1L))
			.andExpect(jsonPath("$.results.title").value("New Book"))
			.andExpect(jsonPath("$.results.author").value("John Doe"))
			.andExpect(jsonPath("$.results.description").value("A brand new book"))
			.andExpect(jsonPath("$.results.copies").value(10))
			.andExpect(jsonPath("$.results.category").value("Fiction"))
			.andExpect(jsonPath("$.results.image").value("some-image-url"));

		verify(bookService).saveBook(any(BookRequest.class));
	}

	@Test
	void patchQuantity_increase_returnsUpdatedDto() throws Exception {
		BookResponse dto = new BookResponse();
		dto.setBookId(1L);
		dto.setTitle("Title");
		dto.setAuthor("Author");
		dto.setCopies(4);
		dto.setCopiesAvailable(4);

		when(bookService.updateBookQuantity(eq(1L), argThat(req -> req.getDelta() == 1))).thenReturn(dto);

		mockMvc
			.perform(patch("/api/books/{id}/quantity", 1L).contentType(MediaType.APPLICATION_JSON)
				.content("{\"delta\":1}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.bookId").value(1L))
			.andExpect(jsonPath("$.results.copies").value(4))
			.andExpect(jsonPath("$.results.copiesAvailable").value(4));

		verify(bookService).updateBookQuantity(eq(1L), argThat(req -> req.getDelta() == 1));
	}

	@Test
	void testDeleteBook() throws Exception {
		mockMvc.perform(delete("/api/books/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(bookService).deleteBook(1L);
	}

	@Test
	void searchBooks_ok_allCategory() throws Exception {
		String query = "someTitle";
		String category = "All";
		int page = 0, size = 10;

		BookResponse r1 = new BookResponse();
		r1.setTitle("SomeTitle here");
		Page<BookResponse> bookPage = new PageImpl<>(List.of(r1), PageRequest.of(page, size), 1);

		when(bookService.searchBooks(query, category, page, size)).thenReturn(bookPage);

		mockMvc
			.perform(get("/api/books/search").param("query", query)
				.param("category", category)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.content[0].title").value("SomeTitle here"));

		verify(bookService).searchBooks(query, category, page, size);
	}

	@Test
	void searchBooks_ok_specificCategory_empty() throws Exception {
		String query = "x";
		String category = "Psychology";
		int page = 0, size = 10;

		Page<BookResponse> empty = Page.empty(PageRequest.of(page, size));

		when(bookService.searchBooks(query, category, page, size)).thenReturn(empty);

		mockMvc
			.perform(get("/api/books/search").param("query", query)
				.param("category", category)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.results.content").isEmpty());

		verify(bookService).searchBooks(query, category, page, size);
	}

}
