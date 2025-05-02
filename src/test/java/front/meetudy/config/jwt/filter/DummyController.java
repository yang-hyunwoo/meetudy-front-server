package front.meetudy.config.jwt.filter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class DummyController {

    @GetMapping("/any-endpoint")
    public ResponseEntity<String> anyEndpoint() {
        return ResponseEntity.ok("ok");
    }
}
