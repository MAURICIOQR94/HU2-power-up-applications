package co.com.pragma.api.services.loanapplication;

import co.com.pragma.api.dto.LoanApplicationRequestDTO;
import co.com.pragma.api.dto.LoanApplicationResponseDTO;
import co.com.pragma.api.dto.common.ErrorDTO;
import org.springdoc.core.fn.builders.operation.Builder;

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
    public static final String CREATED = "Created";
    public static final String NOT_FOUND = "Loan Application not found";
    public static final String ACCESS_DENIED = "Not Authenticated";

    protected Consumer<Builder> findByStatusPaged() {
        return ops -> ops.tag("authentication")
                .operationId("findByStatusPaged").summary("Get loan applications by status filter")
                .description("Get loan applications by status filter with pagination").tags(new String[]{"loan applications"})
                .parameter(createHeader(
                        String.class, ACCEPT, ACCEPT_HEADER, APPLICATION_JSON_VALUE
                ))
                .parameter(createQuery(String.class, "status", "Get loan applications by status filter with pagination"))
                .response(responseBuilder().responseCode("200").description(SUCCESS)
                        .content(
                                contentBuilder()
                                        .schema(schemaBuilder().implementation(LoanApplicationResponseDTO.class))
                                        .example(exampleFindResponse())
                        )
                )
                .response(responseBuilder().responseCode("401").description(ACCESS_DENIED)
                        .implementation(ErrorDTO.class))
                .response(responseBuilder().responseCode("404").description(NOT_FOUND)
                        .implementation(ErrorDTO.class))
                .response(responseBuilder().responseCode("500").description(TECHNICAL_ERROR)
                        .implementation(ErrorDTO.class));
    }

    protected Consumer<Builder> save() {
        return ops -> ops.tag("applications")
                .operationId("save").summary("Create loan application")
                .description("Create loan application").tags(new String[]{"applications"})
                .parameter(createHeader(
                        String.class, ACCEPT, ACCEPT_HEADER, APPLICATION_JSON_VALUE
                ))
                .requestBody(requestBodyBuilder().implementation(LoanApplicationRequestDTO.class))
                .response(responseBuilder().responseCode("201").description(CREATED)
                        .content(
                                contentBuilder()
                                        .schema(schemaBuilder().implementation(LoanApplicationRequestDTO.class))
                                        .example(exampleSaveResponse())
                        )
                )
                .response(responseBuilder().responseCode("401").description(ACCESS_DENIED)
                        .implementation(ErrorDTO.class))
                .response(responseBuilder().responseCode("404").description(NOT_FOUND)
                        .implementation(ErrorDTO.class))
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

    private org.springdoc.core.fn.builders.exampleobject.Builder exampleSaveResponse() {
        return exampleOjectBuilder().value("""
                {
                    "amount": "150.000.000,50",
                    "term": 24,
                    "loanType": {
                        "name": null,
                        "interestRate": null
                    },
                    "status": {
                        "name": null,
                        "description": null
                    },
                    "createdAt": "2025-09-09T20:40:47.0559827"
                }
                """);
    }

    private org.springdoc.core.fn.builders.exampleobject.Builder exampleFindResponse() {
        return exampleOjectBuilder().value("""
                {
                    "size": 1,
                    "totalPages": 2,
                    "page": 1,
                    "totalElements": 2,
                    "data": [
                        {
                            "amount": "15.000.000,50",
                            "term": 72,
                            "user": {
                                "email": "pepe7@gmail.com",
                                "firstName": "Pepe",
                                "lastName": "Perez"
                            },
                            "loanType": {
                                "name": "PERSONAL",
                                "interestRate": 14.0
                            },
                            "status": {
                                "name": "PENDIENTE",
                                "description": "La solicitud fue creada, pero aún no se ha evaluado"
                            },
                            "createdAt": "2025-09-06T11:53:13.549785",
                            "monthlyPayment": "309.086,10"
                        }
                    ]
                }
                """);
    }

}
