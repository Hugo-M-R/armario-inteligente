package br.com.unit.tokseg.armario_inteligente.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class ForbiddenOperationException extends ErrorResponseException {

    public ForbiddenOperationException(String message) {
        super(HttpStatus.FORBIDDEN, ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, message), null);
    }
}
