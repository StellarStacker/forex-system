# Forex Simulator (Java Swing)

Simple desktop Forex simulation app written in plain Java Swing.

## What It Does

- Starts with a fixed main-wallet USD balance (`1000.00`)
- Supports spot-style trading between currency pairs (`USD -> EUR`, `EUR -> GBP`, etc.)
- Deducts source currency and credits target currency in the main wallet
- Supports named sub-accounts and transfers between `Main -> Account` and `Account -> Main`
- Stores all actions as in-memory transactions with timestamp
- Fetches real-time rates using the Frankfurter free exchange-rate API

## Class Structure

```text
src/forex_trading/
  Forex_trading.java
  MainApp.java
  ForexService.java
  User.java
  Wallet.java
  Transaction.java
  TransactionManager.java
```

## Run With javac/java

From project root:

```bash
mkdir -p out
javac -d out src/forex_trading/*.java
java -cp out forex_trading.Forex_trading
```

## Run With Ant (Optional)

```bash
ant clean run
```

## Notes

- In-memory only (no database)
- No login/authentication
- Requires internet access for live exchange rates
