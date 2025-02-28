package backend.controller;

import backend.dto.BookDTO;
import backend.dto.ErrorDTO;
import backend.dto.response.APIResponse;
import backend.exception.BookNotFoundException;
import backend.exception.BookValidationException;
import backend.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<APIResponse<List<BookDTO>>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(
                APIResponse.<List<BookDTO>>builder()
                        .status(SUCCESS)
                        .results(books)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<BookDTO>> getBookById(@PathVariable Long id) {
        BookDTO bookDTO = bookService.getBookById(id);
        return ResponseEntity.ok(
                APIResponse.<BookDTO>builder()
                        .status(SUCCESS)
                        .results(bookDTO)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<APIResponse<BookDTO>> createBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO created = bookService.saveBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                APIResponse.<BookDTO>builder()
                        .status(SUCCESS)
                        .results(created)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<BookDTO>> updateBook(@PathVariable Long id,
                                                           @Valid @RequestBody BookDTO bookDTO) {
        BookDTO updated = bookService.updateBook(id, bookDTO);
        return ResponseEntity.ok(
                APIResponse.<BookDTO>builder()
                        .status(SUCCESS)
                        .results(updated)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(
                APIResponse.<Void>builder()
                        .status(SUCCESS)
                        .build()
        );
    }

    /**
     * Handle custom exceptions
     */
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<APIResponse<Void>> handleNotFoundException(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                APIResponse.<Void>builder()
                        .status(ERROR)
                        .errors(List.of(new ErrorDTO("id", ex.getMessage())))
                        .build()
        );
    }

    @ExceptionHandler(BookValidationException.class)
    public ResponseEntity<APIResponse<Void>> handleValidationException(BookValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                APIResponse.<Void>builder()
                        .status(ERROR)
                        .errors(List.of(new ErrorDTO("validation", ex.getMessage())))
                        .build()
        );
    }

    /**
     * Handle bean validation errors
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<APIResponse<Void>> handleBindException(BindException ex) {
        List<ErrorDTO> errorList = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorDTO(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(
                APIResponse.<Void>builder()
                        .status(ERROR)
                        .errors(errorList)
                        .build()
        );
    }
}

