package co.com.pragma.common.exception;

import co.com.pragma.common.enums.TechnicalExceptionMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

    private final TechnicalExceptionMessage technicalExceptionMessage;

    public TechnicalException(TechnicalExceptionMessage technicalExceptionEnum) {
        super(technicalExceptionEnum.getMessage());
        this.technicalExceptionMessage = technicalExceptionEnum;
    }

    public TechnicalException(String message, TechnicalExceptionMessage technicalExceptionMessage) {
        super(message);
        this.technicalExceptionMessage = technicalExceptionMessage;
    }

    public TechnicalException(Throwable cause, TechnicalExceptionMessage message) {
        super(cause);
        this.technicalExceptionMessage = message;
    }
}
