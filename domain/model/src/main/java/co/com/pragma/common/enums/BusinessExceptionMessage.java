package co.com.pragma.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessExceptionMessage {

    LOAN_TYPE_NOT_FOUND("BE001", "The loan type was not found."),
    APPLICATION_STATUS_NOT_FOUND("BE002", "The application status was not found."),
    LOAN_APPLICATION_NOT_FOUND("BE003", "The loan application was not found."),
    STATUS_ALREADY_SET ("BE004", "The loan application already has the status indicated."),
    CANNOT_CHANGE_STATUS ("BE005", "The status cannot be changed.");

    private final String code;
    private final String message;
}
