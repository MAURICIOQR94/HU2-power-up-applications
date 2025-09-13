package co.com.pragma.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalExceptionMessage {

    INTERNAL_SERVER_ERROR("TE001", "Internal server error"),
    JSON_PROCESSING("TE002", "Error processing request for logs"),

    LOAN_APPLICATION_FIND_BY_ID("TE003", "Error getting loan application by id"),
    LOAN_APPLICATION_SAVE("TE004", "Error saving loan application"),
    LOAN_APPLICATION_UPDATE("TE005", "Error updating loan application"),
    LOAN_APPLICATION_DELETE("TE006", "Error deleting loan application"),

    LOAN_TYPE_FIND_BY_ID("TE007", "Error getting loan type by id"),
    ERROR_SERIALIZING_OBJECT("TE008", "Error serializing object");

    private final String code;
    private final String message;

}
