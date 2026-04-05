package forex_trading;

import java.time.LocalDateTime;

public class Transaction {

    private final String fromCurrency;
    private final String toCurrency;
    private final double amount;
    private final double convertedAmount;
    private final LocalDateTime timestamp;

    public Transaction(String fromCurrency, String toCurrency, double amount, double convertedAmount,
            LocalDateTime timestamp) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
        this.timestamp = timestamp;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public double getAmount() {
        return amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
