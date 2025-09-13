package co.com.pragma.service;

public class LoanCalculator {

    private LoanCalculator() {}

    public static double calculateMonthlyPayment(Double amount, Float annualRate, Integer termInMonths) {
        double monthlyRate = annualRate / 12.0 / 100.0;
        if (monthlyRate == 0) {
            return Math.ceil(amount / termInMonths);
        }
        return Math.ceil(amount * (monthlyRate * Math.pow(1 + monthlyRate, termInMonths))
                / (Math.pow(1 + monthlyRate, termInMonths) - 1));
    }

}
