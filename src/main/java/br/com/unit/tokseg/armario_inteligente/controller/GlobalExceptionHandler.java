package br.com.unit.tokseg.armario_inteligente.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Requisição inválida");
        problem.setDetail("Um ou mais campos são inválidos");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("errors", ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new ValidationFieldError(err.getField(), err.getDefaultMessage()))
                .toList());
        return problem;
    }

    @ExceptionHandler(MethodValidationException.class)
    public ProblemDetail handleMethodValidation(MethodValidationException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Requisição inválida");
        problem.setDetail("Falha de validação");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Requisição inválida");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("about:blank"));
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ProblemDetail handleErrorResponse(ErrorResponseException ex, HttpServletRequest request) {
        ProblemDetail problem = ex.getBody();
        if (problem.getType() == null) {
            problem.setType(URI.create("about:blank"));
        }
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    private record ValidationFieldError(String field, String message) {
    }
}

