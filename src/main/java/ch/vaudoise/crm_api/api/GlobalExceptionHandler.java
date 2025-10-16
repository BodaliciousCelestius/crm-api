package ch.vaudoise.crm_api.api;

import ch.vaudoise.crm_api.model.exception.ApiErrorResponse;
import ch.vaudoise.crm_api.model.exception.NotFoundException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({NoResourceFoundException.class, NotFoundException.class})
  public Mono<ResponseEntity<ApiErrorResponse>> handle404NotFound(
      RuntimeException ex, ServerWebExchange exchange) {
    String path = exchange.getRequest().getPath().value();
    ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage(), path);
    log.error("Resource not found : {}", ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(body));
  }

  @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
  public Mono<ResponseEntity<ApiErrorResponse>> handle400BadRequest(
      RuntimeException ex, ServerWebExchange exchange) {
    String path = exchange.getRequest().getPath().value();
    ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage(), path);
    log.warn(ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public Mono<ResponseEntity<ApiErrorResponse>> handleDuplicateKey(
      DuplicateKeyException ex, ServerWebExchange exchange) {
    String path = exchange.getRequest().getPath().value();
    ApiErrorResponse body =
        ApiErrorResponse.of(
            HttpStatus.BAD_REQUEST,
            "Duplicate key when creating resource : " + ex.getMessage(),
            path);
    log.warn("Duplicate key exception : {}", ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<ApiErrorResponse>> handleValidation(
      WebExchangeBindException ex, ServerWebExchange exchange) {
    String details =
        ex.getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining(", "));
    String path = exchange.getRequest().getPath().value();
    ApiErrorResponse body =
        ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "Validation failed: " + details, path);
    log.warn("Validation failed : {}", ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public Mono<ResponseEntity<ApiErrorResponse>> handleConstraintViolation(
      ConstraintViolationException ex, ServerWebExchange exchange) {
    String details =
        ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.joining(", "));
    String path = exchange.getRequest().getPath().value();
    ApiErrorResponse body =
        ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "Constraint violation: " + details, path);
    log.warn("Constraint violation: {}", ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
  }

  @ExceptionHandler(ServerWebInputException.class)
  public ResponseEntity<ApiErrorResponse> handleServerWebInput(
      ServerWebInputException ex, ServerWebExchange exchange) {
    Throwable cause = ex.getCause();
    String path = exchange.getRequest().getPath().value();

    InvalidFormatException invalidFmt = findCause(cause, InvalidFormatException.class);
    if (invalidFmt != null) {
      String message = getMessage(invalidFmt);

      var body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "Malformed request: " + message, path);
      log.warn(message);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    DateTimeParseException dtpe = findCause(cause, DateTimeParseException.class);
    if (dtpe != null) {
      var body =
          ApiErrorResponse.of(
              HttpStatus.BAD_REQUEST, "Malformed date — expected format 'yyyy-MM-dd'.", path);
      log.warn("Malformed date — expected format 'yyyy-MM-dd' : {}", ex.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    var body =
        ApiErrorResponse.of(
            HttpStatus.BAD_REQUEST, "Malformed field — check request body formatting.", path);
    log.warn("Malformed field — check request body formatting : {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(MethodNotAllowedException.class)
  public Mono<ResponseEntity<ApiErrorResponse>> handleMethodNotAllowed(
      MethodNotAllowedException ex, ServerWebExchange exchange) {
    String path = exchange.getRequest().getPath().value();
    ApiErrorResponse body =
        ApiErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), path);
    log.warn("Method Not Allowed : {}", ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body));
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<ApiErrorResponse>> handleUnexpected(
      Exception ex, ServerWebExchange exchange) {
    String path = exchange.getRequest().getPath().value();
    ApiErrorResponse body =
        ApiErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error: " + ex.getMessage(), path);
    log.error("Unexpected server error", ex);
    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body));
  }

  private <T extends Throwable> T findCause(Throwable t, Class<T> clazz) {
    while (t != null) {
      if (clazz.isInstance(t)) return clazz.cast(t);
      t = t.getCause();
    }
    return null;
  }

  private static String getMessage(InvalidFormatException invalidFmt) {
    String field =
        invalidFmt.getPath().isEmpty() ? "unknown" : invalidFmt.getPath().getFirst().getFieldName();
    String value = String.valueOf(invalidFmt.getValue());
    Class<?> targetType = invalidFmt.getTargetType();

    String expectedFormat =
        switch (targetType.getSimpleName()) {
          case "LocalDate" -> "yyyy-MM-dd";
          case "LocalDateTime" -> "yyyy-MM-dd'T'HH:mm:ss";
          case "LocalTime" -> "HH:mm[:ss]";
          case "OffsetDateTime" -> "yyyy-MM-dd'T'HH:mm:ssXXX";
          default -> targetType.getSimpleName();
        };

      return String.format(
          "Field '%s' has invalid value '%s' — expected format '%s'.",
          field, value, expectedFormat);
  }
}
