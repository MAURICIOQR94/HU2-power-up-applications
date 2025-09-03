package co.com.pragma.api.handlers;

import co.com.pragma.common.exception.GeneralException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static co.com.pragma.common.enums.GeneralExceptionMessage.INVALID_BODY_PARAMETER;

@Component
@RequiredArgsConstructor
@Log4j2
public class ValidatorHandler {

    private final Validator validator;

    private <T> void validateConstraints(Set<ConstraintViolation<T>> constraints) {
        if (!constraints.isEmpty()) {
            log.error(new GeneralException(getMessage(constraints), INVALID_BODY_PARAMETER));
            throw new GeneralException(getMessage(constraints), INVALID_BODY_PARAMETER);
        }
    }

    public <T> void validateObject(T object) {
        Set<ConstraintViolation<T>> constraints = validator.validate(object);
        validateConstraints(constraints);
    }

    private <T> String getMessage(Set<ConstraintViolation<T>> constraintViolations) {
        return constraintViolations.stream()
                .map(c -> String.join(" ", c.getPropertyPath().toString(), c.getMessage()))
                .collect(Collectors.joining(", "));
    }

}
