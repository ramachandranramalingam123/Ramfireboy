package com.beyontec.mdcp.company.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.io.FileNotFoundException;


@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(ex.getMessage());
        return setStatusAndMessage(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(value = FileNotFoundException.class)
    public ResponseEntity<Object> fileNotFoundException(FileNotFoundException ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.NOT_FOUND, ex, request);
    }


    @ExceptionHandler(value = ArrayIndexOutOfBoundsException.class)
    public ResponseEntity<Object> arrayIndexOutOfBoundsException(ArrayIndexOutOfBoundsException ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(value = ClassCastException.class)
    public ResponseEntity<Object> classCastException(ClassCastException ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Object> illegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<Object> nullPointerException(NullPointerException ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(value = NoSuchMethodError.class)
    public ResponseEntity<Object> nullPointerException(NoSuchMethodError ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.NOT_FOUND, null, request);
    }

    @ExceptionHandler(value = NumberFormatException.class)
    public ResponseEntity<Object> numberFormatException(NumberFormatException ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.BAD_REQUEST, ex, request);
    }

    /*@ExceptionHandler(value = AssertionError.class)
    public ResponseEntity<Object> assertionError(AssertionError ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(value = ExceptionInInitializerError.class)
    public ResponseEntity<Object> exceptionInInitializerError(ExceptionInInitializerError ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(value = StackOverflowError.class)
    public ResponseEntity<Object> stackOverflowError(StackOverflowError ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(value = NoClassDefFoundError.class)
    public ResponseEntity<Object> noClassDefFoundError(NoClassDefFoundError ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }
*/
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Object> resourceNotFoundException(RuntimeException ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<Object> illegalStateException(IllegalStateException ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Object> resourceNotFoundException(ConstraintViolationException ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> exception(Exception ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(value = BadDataExceptionHandler.class)
    public ResponseEntity<Object> badDataExceptionHandler(BadDataExceptionHandler ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<Object> accessDeniedExceptionHandler(AccessDeniedException ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.FORBIDDEN, ex, request);
    }

    @ExceptionHandler(value = InvalidURLExpception.class)
    public ResponseEntity<Object> urlExpiredExpception(InvalidURLExpception ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.GONE, ex, request);
    }

    @ExceptionHandler(value = NoDataFoundException.class)
    public ResponseEntity<Object> noDataFoundException(NoDataFoundException ex, WebRequest request) {
        return setStatusAndMessage(HttpStatus.NO_CONTENT, ex, request);
    }

    private ResponseEntity<Object> setStatusAndMessage(HttpStatus status, Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), status, request);
    }

}
