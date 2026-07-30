package poker.error;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@Log4j2
public class ExceptionInterceptor {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> wrapResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity
            .status(ex.getStatusCode())
            .body(ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> wrapGeneralException(Exception ex) {
        log.error("{}: {}. Place: {}", ex.getClass(), ex.getMessage(), ex.getStackTrace()[0]);

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}
