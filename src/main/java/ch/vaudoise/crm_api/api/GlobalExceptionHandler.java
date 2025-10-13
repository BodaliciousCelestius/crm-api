package ch.vaudoise.crm_api.api;

import ch.vaudoise.crm_api.model.exception.ApiErrorResponse;
import ch.vaudoise.crm_api.model.exception.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleNotFound(NotFoundException ex, ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage(), path);
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(body));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleDuplicateKey(DuplicateKeyException ex, ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "Duplicate key when creating resource : "+ex.getMessage(), path);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }


    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleValidation(WebExchangeBindException ex, ServerWebExchange exchange) {
        String details = ex.getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        String path = exchange.getRequest().getPath().value();
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "Validation failed: " + details, path);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleConstraintViolation(ConstraintViolationException ex, ServerWebExchange exchange) {
        String details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        String path = exchange.getRequest().getPath().value();
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "Constraint violation: " + details, path);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleBadRequest(ServerWebInputException ex, ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "Malformed request body or parameter: " + ex.getMessage(), path);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleUnexpected(Exception ex, ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error: " + ex.getMessage(), path);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body));
    }
}
