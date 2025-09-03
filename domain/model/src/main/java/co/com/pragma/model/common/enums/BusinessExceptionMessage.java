package co.com.pragma.model.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessExceptionMessage {

    LOAN_TYPE_NOT_FOUND("BE001", "The loan type was not found.");

    private final String code;
    private final String message;
}
