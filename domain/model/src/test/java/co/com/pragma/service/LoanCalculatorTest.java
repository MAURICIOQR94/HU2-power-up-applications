package co.com.pragma.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanCalculatorTest {

    @Test
    void calculateMonthlyPaymentWithPositiveRate() {
        Double amount = 1000000.0;
        Float annualRate = 12.0f;
        Integer termInMonths = 24;

        double expectedPayment = 47074.0;

        double actualPayment = LoanCalculator.calculateMonthlyPayment(amount, annualRate, termInMonths);

        assertEquals(expectedPayment, actualPayment);
    }

    @Test
    void calculateMonthlyPaymentWithoutRate() {
        Double amount = 1000000.0;
        Float annualRate = 0.0f;
        Integer termInMonths = 24;

        double expectedPayment = 41667.0;

        double actualPayment = LoanCalculator.calculateMonthlyPayment(amount, annualRate, termInMonths);

        assertEquals(expectedPayment, actualPayment);
    }

}