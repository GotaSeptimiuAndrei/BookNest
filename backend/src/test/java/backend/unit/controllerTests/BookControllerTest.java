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
import org.springframework.http.MediaType;
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
		BookRequest requestDto = new BookRequest();
		requestDto.setTitle("New Book");
		requestDto.setAuthor("John Doe");
		requestDto.setDescription("A brand new book");
		requestDto.setCopies(10);
		requestDto.setCategory("Fiction");
		requestDto.setImage("some-image-url");

		BookResponse responseDto = new BookResponse();
		responseDto.setBookId(1L);
		responseDto.setTitle("New Book");
		responseDto.setAuthor("John Doe");
		responseDto.setDescription("A brand new book");
		responseDto.setCopies(10);
		responseDto.setCategory("Fiction");
		responseDto.setImage("some-image-url");

		when(bookService.saveBook(any(BookRequest.class))).thenReturn(responseDto);

		mockMvc
			.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
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
	void testUpdateBook() throws Exception {
		BookRequest requestDto = new BookRequest();
		requestDto.setTitle("Updated Title");
		requestDto.setAuthor("Jane Doe");
		requestDto.setDescription("Updated description");
		requestDto.setCopies(5);
		requestDto.setCategory("History");
		requestDto.setImage("updated-image-url");

		BookResponse updatedDto = new BookResponse();
		updatedDto.setBookId(1L);
		updatedDto.setTitle("Updated Title");
		updatedDto.setAuthor("Jane Doe");
		updatedDto.setDescription("Updated description");
		updatedDto.setCopies(5);
		updatedDto.setCategory("History");
		updatedDto.setImage("updated-image-url");

		when(bookService.updateBook(eq(1L), any(BookRequest.class))).thenReturn(updatedDto);

		mockMvc
			.perform(put("/api/books/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.bookId").value(1L))
			.andExpect(jsonPath("$.results.title").value("Updated Title"))
			.andExpect(jsonPath("$.results.author").value("Jane Doe"))
			.andExpect(jsonPath("$.results.description").value("Updated description"))
			.andExpect(jsonPath("$.results.copies").value(5))
			.andExpect(jsonPath("$.results.category").value("History"))
			.andExpect(jsonPath("$.results.image").value("updated-image-url"));

		verify(bookService).updateBook(eq(1L), any(BookRequest.class));
	}

	@Test
	void testDeleteBook() throws Exception {
		mockMvc.perform(delete("/api/books/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(bookService).deleteBook(1L);
	}

	@Test
	void testSearchBooks_ValidQuery() throws Exception {
		String query = "someTitle";
		BookResponse book1 = new BookResponse();
		book1.setBookId(10L);
		book1.setTitle("SomeTitle here");

		BookResponse book2 = new BookResponse();
		book2.setBookId(20L);
		book2.setTitle("Another Book with SomeTitle inside");

		when(bookService.searchBooks(query)).thenReturn(List.of(book1, book2));

		mockMvc.perform(get("/api/books/search").param("query", query))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results[0].title").value("SomeTitle here"))
			.andExpect(jsonPath("$.results[1].title").value("Another Book with SomeTitle inside"));

		verify(bookService).searchBooks(query);
	}

	@Test
	void testSearchBooks_EmptyQuery() throws Exception {
		String query = "";
		when(bookService.searchBooks(query)).thenReturn(List.of());

		mockMvc.perform(get("/api/books/search").param("query", query))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results").isEmpty());

		verify(bookService).searchBooks(query);
	}

}
