package co.com.pragma.usecase.getloanapplications;

import co.com.pragma.domain.gateways.restconsumer.ExternalService;
import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.pageresult.PageResult;
import co.com.pragma.model.user.User;
import co.com.pragma.service.LoanCalculator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class GetLoanApplicationsUseCase {

    private final LoanApplicationRepository loanRepository;
    private final ApplicationStatusRepository statusRepository;
    private final LoanTypeRepository typeRepository;
    private final ExternalService externalService;

    public Mono<PageResult<LoanApplication>> findByStatusPaged(List<String> statuses, int page, int size) {
        return statusRepository.findByNameIn(
                        statuses.stream().map(String::toUpperCase).toList())
                .map(ApplicationStatus::getId)
                .collectList()
                .flatMap(ids -> {
                    if (ids.isEmpty()) {
                        return Mono.just(PageResult.<LoanApplication>builder().data(List.of()).page(page).size(size).totalPages(0).totalElements(0L).build());
                    }
                    Flux<LoanApplication> data = loanRepository.findByIdStatusInPaged(ids, page, size)
                            .flatMap(this::enrichApplication)
                            .map(application -> {
                                Double monthlyPayment = LoanCalculator.calculateMonthlyPayment(
                                        application.getAmount(),
                                        application.getLoanType().getInterestRate(),
                                        application.getTerm());
                                application.setMonthlyPayment(monthlyPayment);

                                return application;
                            });
                    Mono<Long> total = loanRepository.countByIdStatusIn(ids);

                    return Mono.zip(data.collectList(), total)
                            .map(tuple -> {
                                List<LoanApplication> items = tuple.getT1();
                                long totalElements = tuple.getT2();
                                int totalPages = (int) Math.ceil((double) totalElements / size);

                                return PageResult.<LoanApplication>builder().data(items).page(page).size(size).totalElements(totalElements).totalPages(totalPages).build();
                            });
                });
    }

    private Mono<LoanApplication> enrichApplication(LoanApplication loanApplication) {
        Mono<ApplicationStatus> statusMono = statusRepository.findById(loanApplication.getStatus().getId());
        Mono<LoanType> typeMono = typeRepository.findById(loanApplication.getLoanType().getId());
        Mono<User> userMono = externalService.getUserByEmail(loanApplication.getEmail());

        return Mono.zip(statusMono, typeMono, userMono)
                .map(tuple -> {
                    loanApplication.setStatus(tuple.getT1());
                    loanApplication.setLoanType(tuple.getT2());
                    loanApplication.setUser(tuple.getT3());

                    return loanApplication;
                })
                .defaultIfEmpty(loanApplication);
    }

}
