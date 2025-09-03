package co.com.pragma.common.exception;

import co.com.pragma.common.enums.GeneralExceptionMessage;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final GeneralExceptionMessage generalExceptionMessage;

    public GeneralException(GeneralExceptionMessage generalExceptionMessage) {
        super(generalExceptionMessage.getMessage());
        this.generalExceptionMessage = generalExceptionMessage;
    }

    public GeneralException(String message, GeneralExceptionMessage generalExceptionMessage) {
        super(message);
        this.generalExceptionMessage = generalExceptionMessage;
    }
}
