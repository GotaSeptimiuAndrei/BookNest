package backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = { "http://localhost:3000", "https://booknestlibrary.netlify.app" })
@RestController
@RequestMapping("/api/test")
public class TestController {

	@GetMapping
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("Hello World");
	}

}
