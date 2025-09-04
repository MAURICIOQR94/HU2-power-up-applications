package co.com.pragma.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GeneralExceptionMessage {

    BAD_REQUEST_BODY("GE001", "Error in body request", 400),
    HEADER_MISSING_ERROR("GE002", "Missing parameters in the header", 400),
    ACCEPT_HEADER_INVALID("GE003", "Invalid value for Accept header", 400),
    PARAM_MISSING_ERROR("GE004", "Missing parameters", 400),
    PARAM_WRONG_VALUE_ERROR("GE005", "Wrong value for parameter", 400),
    INVALID_BODY_PARAMETER("GE006", "Incomplete request data", 400),
    ACCESS_DENIED("GE007", "Access Denied", 401),
    INVALID_JWT("GE007", "Invalid or expired JWT token", 401);

    private final String code;
    private final String message;
    private final int statusCode;

}
