package mr.anetat.medicamentsapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReferenceDataDuplicateException extends RuntimeException {

    public ReferenceDataDuplicateException(String message) {
        super(message);
    }
}

