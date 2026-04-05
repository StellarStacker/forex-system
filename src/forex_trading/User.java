package forex_trading;

public class User {

    private final String username;
    private double usdBalance;

    public User(String username, double usdBalance) {
        this.username = username;
        this.usdBalance = usdBalance;
    }

    public String getUsername() {
        return username;
    }

    public double getUsdBalance() {
        return usdBalance;
    }

    public void addUsd(double amount) {
        usdBalance += amount;
    }

    public void deductUsd(double amount) {
        usdBalance -= amount;
    }
}
