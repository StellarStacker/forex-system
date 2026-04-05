package forex_trading;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForexService {

    private static final String API_URL = "https://api.frankfurter.app/latest";

    private final User user;
    private final Wallet wallet;
    private final TransactionManager transactionManager;
    private final HttpClient httpClient;
    private final Map<String, Wallet> accounts;

    public ForexService(User user, Wallet wallet, TransactionManager transactionManager) {
        this.user = user;
        this.wallet = wallet;
        this.transactionManager = transactionManager;
        this.httpClient = HttpClient.newHttpClient();
        this.accounts = new LinkedHashMap<>();
    }

    public Transaction trade(String fromCurrency, String toCurrency, double amount) throws IOException, InterruptedException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }

        String from = normalizeCurrency(fromCurrency);
        String to = normalizeCurrency(toCurrency);

        if (from.equals(to)) {
            throw new IllegalArgumentException("From and To currencies must be different.");
        }

        ensureMainFunds(from, amount);

        double rate = fetchExchangeRate(from, to);
        double convertedAmount = amount * rate;

        deductMainBalance(from, amount);
        addMainBalance(to, convertedAmount);

        Transaction transaction = new Transaction(from, to, amount, convertedAmount, LocalDateTime.now());
        transactionManager.addTransaction(transaction);

        return transaction;
    }

    public void createAccount(String accountName) {
        String key = normalizeAccountName(accountName);
        if (accounts.containsKey(key)) {
            throw new IllegalArgumentException("Account already exists.");
        }
        accounts.put(key, new Wallet());
    }

    public List<String> getAccountNames() {
        return new ArrayList<>(accounts.keySet());
    }

    public Transaction transferMainToAccount(String accountName, String currency, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
        String accountKey = normalizeAccountName(accountName);
        Wallet accountWallet = accounts.get(accountKey);
        if (accountWallet == null) {
            throw new IllegalArgumentException("Account not found.");
        }

        String cur = normalizeCurrency(currency);
        ensureMainFunds(cur, amount);

        deductMainBalance(cur, amount);
        accountWallet.addBalance(cur, amount);

        Transaction transaction = new Transaction("MAIN-" + cur, accountKey + "-" + cur,
                amount, amount, LocalDateTime.now());
        transactionManager.addTransaction(transaction);
        return transaction;
    }

    public Transaction transferAccountToMain(String accountName, String currency, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
        String accountKey = normalizeAccountName(accountName);
        Wallet accountWallet = accounts.get(accountKey);
        if (accountWallet == null) {
            throw new IllegalArgumentException("Account not found.");
        }

        String cur = normalizeCurrency(currency);
        double accountBalance = accountWallet.getBalance(cur);
        if (accountBalance < amount) {
            throw new IllegalArgumentException("Insufficient account balance.");
        }

        accountWallet.deductBalance(cur, amount);
        addMainBalance(cur, amount);

        Transaction transaction = new Transaction(accountKey + "-" + cur, "MAIN-" + cur,
                amount, amount, LocalDateTime.now());
        transactionManager.addTransaction(transaction);
        return transaction;
    }

    public Map<String, Double> getMainBalances() {
        Map<String, Double> balances = new LinkedHashMap<>();
        balances.put("USD", user.getUsdBalance());
        for (Map.Entry<String, Double> entry : wallet.getBalances().entrySet()) {
            balances.put(entry.getKey(), entry.getValue());
        }
        return balances;
    }

    public Map<String, Double> getAccountBalances(String accountName) {
        String key = normalizeAccountName(accountName);
        Wallet accountWallet = accounts.get(key);
        if (accountWallet == null) {
            throw new IllegalArgumentException("Account not found.");
        }
        return accountWallet.getBalances();
    }

    private void ensureMainFunds(String currency, double amount) {
        if ("USD".equals(currency)) {
            if (user.getUsdBalance() < amount) {
                throw new IllegalArgumentException("Insufficient USD balance in main wallet.");
            }
            return;
        }

        double balance = wallet.getBalance(currency);
        if (balance < amount) {
            throw new IllegalArgumentException("Insufficient " + currency + " balance in main wallet.");
        }
    }

    private void deductMainBalance(String currency, double amount) {
        if ("USD".equals(currency)) {
            user.deductUsd(amount);
        } else {
            wallet.deductBalance(currency, amount);
        }
    }

    private void addMainBalance(String currency, double amount) {
        if ("USD".equals(currency)) {
            user.addUsd(amount);
        } else {
            wallet.addBalance(currency, amount);
        }
    }

    private String normalizeCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency is required.");
        }
        return currency.trim().toUpperCase();
    }

    private String normalizeAccountName(String accountName) {
        if (accountName == null || accountName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account name is required.");
        }
        return accountName.trim();
    }

    public User getUser() {
        return user;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    private double fetchExchangeRate(String fromCurrency, String toCurrency) throws IOException, InterruptedException {
        String query = "?from=" + URLEncoder.encode(fromCurrency, StandardCharsets.UTF_8)
                + "&to=" + URLEncoder.encode(toCurrency, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + query))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Rate API returned non-200 status: " + response.statusCode());
        }

        return parseRate(response.body(), toCurrency);
    }

    private double parseRate(String responseBody, String toCurrency) throws IOException {
        String patternText = "\\\"" + Pattern.quote(toCurrency) + "\\\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)";
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(responseBody);
        if (!matcher.find()) {
            throw new IOException("Unable to parse exchange rate from API response.");
        }
        return Double.parseDouble(matcher.group(1));
    }
}
