package backend.unit.controllerTests;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import backend.controller.BookLoanController;
import backend.dto.response.CurrentLoansResponse;
import backend.exception.BookLoanException;
import backend.model.BookLoan;
import backend.service.BookLoanService;
import backend.utils.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(BookLoanController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookLoanControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BookLoanService bookLoanService;

	private MockedStatic<JwtUtils> jwtUtilsMock;

	@BeforeEach
	void setUp() {
		jwtUtilsMock = Mockito.mockStatic(JwtUtils.class);
		jwtUtilsMock.when(() -> JwtUtils.extractUsername(Mockito.anyString())).thenReturn("testUser");
	}

	@AfterEach
	void tearDown() {
		jwtUtilsMock.close();
	}

	@Test
	void testGetCurrentLoansByUser_Success() throws Exception {
		CurrentLoansResponse response1 = new CurrentLoansResponse(null, 3);
		CurrentLoansResponse response2 = new CurrentLoansResponse(null, 1);

		when(bookLoanService.currentLoansByUser("testUser")).thenReturn(List.of(response1, response2));

		mockMvc.perform(get("/api/loans").header("Authorization", "Bearer fake_jwt_token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.length()").value(2))
			.andExpect(jsonPath("$.results[0].daysLeft").value(3))
			.andExpect(jsonPath("$.results[1].daysLeft").value(1));
	}

	@Test
	void testGetCurrentLoansCount_Success() throws Exception {
		when(bookLoanService.currentLoansCount("testUser")).thenReturn(5);

		mockMvc.perform(get("/api/loans/count").header("Authorization", "Bearer fake_jwt_token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results").value(5));
	}

	@Test
	void testLoanBook_Success() throws Exception {
		Long bookId = 1L;
		BookLoan mockLoan = new BookLoan();
		mockLoan.setLoanId(100L);
		mockLoan.setBookId(bookId);
		mockLoan.setUsername("testUser");
		mockLoan.setLoanDate(LocalDate.now());
		mockLoan.setReturnDate(LocalDate.now().plusDays(3));

		when(bookLoanService.loanBook("testUser", bookId)).thenReturn(mockLoan);

		mockMvc.perform(post("/api/loans/loan/{bookId}", bookId).header("Authorization", "Bearer fake_jwt_token"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results.loanId").value(100))
			.andExpect(jsonPath("$.results.bookId").value(1))
			.andExpect(jsonPath("$.results.username").value("testUser"));
	}

	@Test
	void testLoanBook_BookLoanException() throws Exception {
		Long bookId = 999L;
		doThrow(new BookLoanException("Book not available")).when(bookLoanService).loanBook("testUser", bookId);

		mockMvc.perform(post("/api/loans/loan/{bookId}", bookId).header("Authorization", "Bearer fake_jwt_token"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].field").value("loan"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("Book not available"));
	}

	@Test
	void testReturnBook_Success() throws Exception {
		Long bookId = 1L;

		mockMvc.perform(delete("/api/loans/return/{bookId}", bookId).header("Authorization", "Bearer fake_jwt_token"))
			.andExpect(status().isNoContent());
	}

	@Test
	void testReturnBook_BookLoanException() throws Exception {
		Long bookId = 999L;
		doThrow(new BookLoanException("Not loaned")).when(bookLoanService).returnBook("testUser", bookId);

		mockMvc.perform(delete("/api/loans/return/{bookId}", bookId).header("Authorization", "Bearer fake_jwt_token"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("Not loaned"));
	}

	@Test
	void testRenewLoan_Success() throws Exception {
		Long bookId = 1L;

		mockMvc.perform(put("/api/loans/renew/{bookId}", bookId).header("Authorization", "Bearer fake_jwt_token"))
			.andExpect(status().isNoContent());
	}

	@Test
	void testRenewLoan_BookLoanException() throws Exception {
		Long bookId = 1L;
		doThrow(new BookLoanException("Cannot renew loan. The return date has passed.")).when(bookLoanService)
			.renewLoan("testUser", bookId);

		mockMvc.perform(put("/api/loans/renew/{bookId}", bookId).header("Authorization", "Bearer fake_jwt_token"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("error"))
			.andExpect(jsonPath("$.errors[0].errorMessage").value("Cannot renew loan. The return date has passed."));
	}

	@Test
	void testIsBookLoanedByUser_Success() throws Exception {
		Long bookId = 1L;
		when(bookLoanService.isBookLoanedByUser("testUser", bookId)).thenReturn(true);

		mockMvc.perform(get("/api/loans/is-loaned/{bookId}", bookId).header("Authorization", "Bearer fake_jwt_token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.results").value(true));
	}

}
