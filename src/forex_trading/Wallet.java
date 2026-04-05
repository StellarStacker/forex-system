package forex_trading;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Wallet {

    private final Map<String, Double> balances = new LinkedHashMap<>();

    public void addBalance(String currency, double amount) {
        String key = currency.toUpperCase();
        double current = balances.getOrDefault(key, 0.0);
        balances.put(key, current + amount);
    }

    public double getBalance(String currency) {
        return balances.getOrDefault(currency.toUpperCase(), 0.0);
    }

    public void deductBalance(String currency, double amount) {
        String key = currency.toUpperCase();
        double current = balances.getOrDefault(key, 0.0);
        balances.put(key, current - amount);
    }

    public Map<String, Double> getBalances() {
        return Collections.unmodifiableMap(balances);
    }
}
