package forex_trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionManager {

    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
