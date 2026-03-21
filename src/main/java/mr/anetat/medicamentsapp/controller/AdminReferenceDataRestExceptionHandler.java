package mr.anetat.medicamentsapp.controller;

import mr.anetat.medicamentsapp.exception.ReferenceDataDuplicateException;
import mr.anetat.medicamentsapp.exception.ReferenceDataInUseException;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {
        FormeAdminRestController.class,
        LaboratoireAdminRestController.class,
        MoleculeAdminRestController.class,
        UniteDosageAdminRestController.class
})
public class AdminReferenceDataRestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return buildProblemDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ReferenceDataDuplicateException.class)
    public ProblemDetail handleDuplicate(ReferenceDataDuplicateException ex) {
        return buildProblemDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ReferenceDataInUseException.class)
    public ProblemDetail handleInUse(ReferenceDataInUseException ex) {
        return buildProblemDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(status.getReasonPhrase());
        return problemDetail;
    }
}

