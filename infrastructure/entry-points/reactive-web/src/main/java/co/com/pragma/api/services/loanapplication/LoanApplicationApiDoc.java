package co.com.pragma.api.services.loanapplication;

import co.com.pragma.api.dto.LoanApplicationRequestDTO;
import co.com.pragma.api.dto.ErrorDTO;
import org.springdoc.core.fn.builders.operation.Builder;
import co.com.pragma.api.mapper.ResponseDTO;

import java.util.function.Consumer;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.exampleobject.Builder.exampleOjectBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

public sealed class LoanApplicationApiDoc permits LoanApplicationRouterRest {

    public static final String BUSINESS_ERROR = "Business Error";
    public static final String TECHNICAL_ERROR = "Technical Error";
    public static final String SUCCESS = "Success";
    public static final String ACCEPT_HEADER = "Accept Header";
    public static final String ACCEPT = "Accept";

    protected Consumer<Builder> save() {
        return ops -> ops.tag("applications")
                .operationId("save").summary("Create loan application")
                .description("Create loan application").tags(new String[]{"applications"})
                .parameter(createHeader(
                        String.class, ACCEPT, ACCEPT_HEADER, APPLICATION_JSON_VALUE
                ))
                .requestBody(requestBodyBuilder().implementation(LoanApplicationRequestDTO.class))
                .response(responseBuilder().responseCode("200").description(SUCCESS)
                        .implementation(ResponseDTO.class)
                        .content(
                                contentBuilder()
                                        .schema(schemaBuilder().implementation(ResponseDTO.class))
                                        .example(exampleResponse())
                        )
                )
                .response(responseBuilder().responseCode("409").description(BUSINESS_ERROR)
                        .implementation(ErrorDTO.class))
                .response(responseBuilder().responseCode("500").description(TECHNICAL_ERROR)
                        .implementation(ErrorDTO.class));
    }

    private <T> org.springdoc.core.fn.builders.parameter.Builder createHeader(Class<T> clazz,
                                                                              String name,
                                                                              String description,
                                                                              String example) {
        return parameterBuilder().in(HEADER).implementation(clazz).required(true).name(name).description(description)
                .example(example);
    }

    private <T> org.springdoc.core.fn.builders.parameter.Builder createQuery(Class<T> clazz,
                                                                             String name,
                                                                             String description) {
        return parameterBuilder().in(QUERY).implementation(clazz).required(true).name(name).description(description);
    }

    private org.springdoc.core.fn.builders.exampleobject.Builder exampleResponse() {
        return exampleOjectBuilder().value("""
                {
                    "data": {
                        "idUser": 1,
                        "firstname": "Mauricio",
                        "lastname": "Quintero",
                        "birthDate":30-01-94,
                        "email": "mauroqr94@gmail.com"
                    }
                }
                """);
    }

}
