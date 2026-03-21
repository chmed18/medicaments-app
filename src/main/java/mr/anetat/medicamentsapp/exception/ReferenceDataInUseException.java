package mr.anetat.medicamentsapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReferenceDataInUseException extends RuntimeException {

    public ReferenceDataInUseException(String message) {
        super(message);
    }
}

