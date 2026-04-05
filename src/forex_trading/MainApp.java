package forex_trading;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class MainApp extends JFrame {

    private static final String[] SUPPORTED_CURRENCIES = {"USD", "INR", "EUR", "GBP", "JPY", "AUD", "CAD", "SGD"};

    private final ForexService forexService;

    private final JTextArea mainWalletTextArea;
    private final JTextField tradeAmountField;
    private final JComboBox<String> fromCurrencyDropdown;
    private final JComboBox<String> toCurrencyDropdown;

    private final JTextField accountNameField;
    private final JComboBox<String> transferDirectionDropdown;
    private final JComboBox<String> accountDropdown;
    private final JComboBox<String> transferCurrencyDropdown;
    private final JTextField transferAmountField;
    private final JTextArea accountWalletTextArea;

    private final DefaultTableModel transactionTableModel;

    public MainApp() {
        User user = new User("demo-user", 1000.0);
        Wallet wallet = new Wallet();
        TransactionManager transactionManager = new TransactionManager();
        this.forexService = new ForexService(user, wallet, transactionManager);

        setTitle("Forex Simulator");
        setSize(850, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color appBackground = new Color(242, 242, 242);
        Color panelBackground = Color.WHITE;
        Color textColor = Color.BLACK;
        Color buttonGray = new Color(215, 215, 215);

        getContentPane().setBackground(appBackground);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Forex Simulator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(textColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 8, 8, 8));
        add(titleLabel, BorderLayout.NORTH);

        JPanel dashboardPanel = new JPanel(new BorderLayout(10, 10));
        dashboardPanel.setBackground(appBackground);

        JPanel balancePanel = new JPanel(new GridLayout(2, 1, 8, 8));
        balancePanel.setBackground(panelBackground);
        balancePanel.setBorder(BorderFactory.createTitledBorder("Main Wallet Balances"));

        JLabel infoLabel = new JLabel("Trade currencies in main wallet and transfer to named accounts.");
        mainWalletTextArea = new JTextArea(8, 24);
        mainWalletTextArea.setEditable(false);
        mainWalletTextArea.setBackground(new Color(250, 250, 250));
        mainWalletTextArea.setForeground(textColor);
        mainWalletTextArea.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));

        balancePanel.add(infoLabel);
        balancePanel.add(new JScrollPane(mainWalletTextArea));

        JPanel tradePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        tradePanel.setBackground(panelBackground);
        tradePanel.setBorder(BorderFactory.createTitledBorder("Trade (Spot Simulation)"));

        tradeAmountField = new JTextField(8);
        fromCurrencyDropdown = new JComboBox<>(SUPPORTED_CURRENCIES);
        toCurrencyDropdown = new JComboBox<>(SUPPORTED_CURRENCIES);
        toCurrencyDropdown.setSelectedItem("EUR");

        JButton tradeButton = new JButton("Trade");
        JButton refreshButton = new JButton("Refresh");
        tradeButton.setBackground(buttonGray);
        refreshButton.setBackground(buttonGray);

        tradePanel.add(new JLabel("Amount:"));
        tradePanel.add(tradeAmountField);
        tradePanel.add(new JLabel("From:"));
        tradePanel.add(fromCurrencyDropdown);
        tradePanel.add(new JLabel("To:"));
        tradePanel.add(toCurrencyDropdown);
        tradePanel.add(tradeButton);
        tradePanel.add(refreshButton);

        dashboardPanel.add(balancePanel, BorderLayout.CENTER);
        dashboardPanel.add(tradePanel, BorderLayout.SOUTH);

        JPanel accountsPanel = new JPanel(new BorderLayout(10, 10));
        accountsPanel.setBackground(appBackground);

        JPanel createAccountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        createAccountPanel.setBackground(panelBackground);
        createAccountPanel.setBorder(BorderFactory.createTitledBorder("Accounts"));
        accountNameField = new JTextField(12);
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBackground(buttonGray);
        createAccountPanel.add(new JLabel("Account Name:"));
        createAccountPanel.add(accountNameField);
        createAccountPanel.add(createAccountButton);

        JPanel transferPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        transferPanel.setBackground(panelBackground);
        transferPanel.setBorder(BorderFactory.createTitledBorder("Transfer Funds"));

        transferDirectionDropdown = new JComboBox<>(new String[]{"Main -> Account", "Account -> Main"});
        accountDropdown = new JComboBox<>();
        transferCurrencyDropdown = new JComboBox<>(SUPPORTED_CURRENCIES);
        transferAmountField = new JTextField(8);
        JButton transferButton = new JButton("Transfer");
        transferButton.setBackground(buttonGray);

        transferPanel.add(new JLabel("Direction:"));
        transferPanel.add(transferDirectionDropdown);
        transferPanel.add(new JLabel("Account:"));
        transferPanel.add(accountDropdown);
        transferPanel.add(new JLabel("Currency:"));
        transferPanel.add(transferCurrencyDropdown);
        transferPanel.add(new JLabel("Amount:"));
        transferPanel.add(transferAmountField);
        transferPanel.add(transferButton);

        accountWalletTextArea = new JTextArea(10, 40);
        accountWalletTextArea.setEditable(false);
        accountWalletTextArea.setBackground(new Color(250, 250, 250));
        accountWalletTextArea.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        JScrollPane accountScrollPane = new JScrollPane(accountWalletTextArea);
        accountScrollPane.setBorder(BorderFactory.createTitledBorder("Selected Account Balances"));

        JPanel accountTop = new JPanel(new BorderLayout(10, 10));
        accountTop.setBackground(appBackground);
        accountTop.add(createAccountPanel, BorderLayout.NORTH);
        accountTop.add(transferPanel, BorderLayout.CENTER);

        accountsPanel.add(accountTop, BorderLayout.NORTH);
        accountsPanel.add(accountScrollPane, BorderLayout.CENTER);

        transactionTableModel = new DefaultTableModel(
                new Object[]{"From", "To", "Amount", "Converted", "Timestamp"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable transactionTable = new JTable(transactionTableModel);
        JScrollPane transactionScrollPane = new JScrollPane(transactionTable);
        transactionScrollPane.setBorder(BorderFactory.createTitledBorder("Transaction History"));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Accounts", accountsPanel);
        tabbedPane.addTab("Transactions", transactionScrollPane);
        add(tabbedPane, BorderLayout.CENTER);

        tradeButton.addActionListener(e -> handleTrade());
        refreshButton.addActionListener(e -> refreshDataView());
        createAccountButton.addActionListener(e -> handleCreateAccount());
        transferButton.addActionListener(e -> handleTransfer());
        accountDropdown.addActionListener(e -> refreshAccountView());

        refreshDataView();
    }

    private void handleTrade() {
        String amountText = tradeAmountField.getText().trim();
        String fromCurrency = String.valueOf(fromCurrencyDropdown.getSelectedItem());
        String toCurrency = String.valueOf(toCurrencyDropdown.getSelectedItem());

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Transaction transaction = forexService.trade(fromCurrency, toCurrency, amount);
            tradeAmountField.setText("");
            refreshDataView();

            String message = String.format(
                    "Traded %.4f %s to %.4f %s",
                    transaction.getAmount(),
                    transaction.getFromCurrency(),
                    transaction.getConvertedAmount(),
                    transaction.getToCurrency()
            );
            JOptionPane.showMessageDialog(this, message, "Trade Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Trade Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateAccount() {
        String accountName = accountNameField.getText().trim();
        try {
            forexService.createAccount(accountName);
            accountNameField.setText("");
            refreshDataView();
            JOptionPane.showMessageDialog(this, "Account created.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Create Account Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTransfer() {
        String accountName = (String) accountDropdown.getSelectedItem();
        String direction = (String) transferDirectionDropdown.getSelectedItem();
        String currency = (String) transferCurrencyDropdown.getSelectedItem();
        String amountText = transferAmountField.getText().trim();

        if (accountName == null || accountName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Create and select an account first.", "No Account",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid transfer amount.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if ("Main -> Account".equals(direction)) {
                forexService.transferMainToAccount(accountName, currency, amount);
            } else {
                forexService.transferAccountToMain(accountName, currency, amount);
            }
            transferAmountField.setText("");
            refreshDataView();
            JOptionPane.showMessageDialog(this, "Transfer completed.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Transfer Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshDataView() {
        Map<String, Double> mainBalances = forexService.getMainBalances();
        if (mainBalances.isEmpty()) {
            mainWalletTextArea.setText("No balances found.");
        } else {
            StringBuilder walletText = new StringBuilder("Main Wallet\n");
            for (Map.Entry<String, Double> entry : mainBalances.entrySet()) {
                walletText.append(entry.getKey())
                        .append(": ")
                        .append(String.format("%.4f", entry.getValue()))
                        .append("\n");
            }
            mainWalletTextArea.setText(walletText.toString());
        }

        List<String> accountNames = forexService.getAccountNames();
        Object selected = accountDropdown.getSelectedItem();
        accountDropdown.removeAllItems();
        for (String accountName : accountNames) {
            accountDropdown.addItem(accountName);
        }
        if (selected != null && accountNames.contains(selected.toString())) {
            accountDropdown.setSelectedItem(selected);
        }

        if (accountDropdown.getItemCount() > 0 && accountDropdown.getSelectedItem() == null) {
            accountDropdown.setSelectedIndex(0);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Transaction> transactions = forexService.getTransactionManager().getTransactions();

        transactionTableModel.setRowCount(0);
        for (Transaction transaction : transactions) {
            transactionTableModel.addRow(new Object[]{
                transaction.getFromCurrency(),
                transaction.getToCurrency(),
                String.format("%.2f", transaction.getAmount()),
                String.format("%.4f", transaction.getConvertedAmount()),
                transaction.getTimestamp().format(dateTimeFormatter)
            });
        }

        refreshAccountView();
    }

    private void refreshAccountView() {
        String accountName = (String) accountDropdown.getSelectedItem();
        if (accountName == null || accountName.isEmpty()) {
            accountWalletTextArea.setText("No account selected.");
            return;
        }

        try {
            Map<String, Double> balances = forexService.getAccountBalances(accountName);
            if (balances.isEmpty()) {
                accountWalletTextArea.setText("Account has no balances yet.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Account: ").append(accountName).append("\n");
            for (Map.Entry<String, Double> entry : balances.entrySet()) {
                sb.append(entry.getKey())
                        .append(": ")
                        .append(String.format("%.4f", entry.getValue()))
                        .append("\n");
            }
            accountWalletTextArea.setText(sb.toString());
        } catch (Exception ex) {
            accountWalletTextArea.setText(ex.getMessage());
        }
    }
}
