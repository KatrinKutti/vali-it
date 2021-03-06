package ee.bcs.valiit.tasks.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BankingRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void createCustomer(String firstName, String lastName, String address) {
        String sql = "INSERT INTO customer(firstname, lastname, address) VALUES (:firstname, :lastname, :address)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("firstname", firstName);
        paramMap.put("lastname", lastName);
        paramMap.put("address", address);
        jdbcTemplate.update(sql, paramMap);
    }

    public void createAccount(String accountNr) {
        String sql = "INSERT INTO account(account_number, balance) VALUES (:accountNr, :balance)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("accountNr", accountNr);
        paramMap.put("balance", BigDecimal.ZERO);
        jdbcTemplate.update(sql, paramMap);
    }

    public int getCustomerId(String firstName, String lastName) {
        Map<String, Object> paramMap = new HashMap<>();
        String sql = "SELECT customer_id FROM customer WHERE firstname = :firstname AND lastname = :lastname";
        paramMap.put("firstname", firstName);
        paramMap.put("lastname", lastName);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    public int getAccountId(String firstName, String lastName) {
        Map<String, Object> paramMap = new HashMap<>();
        String sql = "SELECT account_id FROM account WHERE customer_id = :customer_id";
        paramMap.put("customer_id", getCustomerId(firstName, lastName));
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    public void customerToAccount(int dbCustomer_id, int dbAccount_id) {
        String sql = "UPDATE account SET customer_id = :customer_id WHERE account_id = :account_id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("customer_id", dbCustomer_id);
        paramMap.put("account_id", dbAccount_id);
        jdbcTemplate.update(sql, paramMap);
    }

    public BigDecimal getBalance(String accountNr) {
        String sql = "SELECT balance FROM account WHERE account_number = :account_number";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("account_number", accountNr);
        return jdbcTemplate.queryForObject(sql, paramMap, BigDecimal.class);
    }

    public void updateBalance(String accountNr, BigDecimal dbBalance) {
        String sql = "UPDATE account SET balance = :balance WHERE account_number = :account_number";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("account_number", accountNr);
        paramMap.put("balance", dbBalance);
        jdbcTemplate.update(sql, paramMap);
    }

    public void addToHistory(String accountNr, BigDecimal deposit, BigDecimal withdraw, String fromAccount, String toAccount, BigDecimal transfer, Object timestamp) {
        String sql = "INSERT INTO transaction_history(account_number, deposit, withdrawal, from_account, to_account, transfer, timestamp) VALUES (:account_number, :deposit, :withdrawal, :from_account, :to_account, :transfer, :timestamp)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("account_number", accountNr);
        paramMap.put("deposit", deposit);
        paramMap.put("withdrawal", withdraw);
        paramMap.put("to_account", toAccount);
        paramMap.put("from_account", fromAccount);
        paramMap.put("transfer", transfer);
        paramMap.put("timestamp", LocalDateTime.now().toString());
        jdbcTemplate.update(sql, paramMap);
    }

    public List<Account> customerAccounts(int customerId) {
        String sql = "SELECT * FROM account WHERE customer_id = :customer_id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("customer_id", customerId);
        List<Account> accountList = jdbcTemplate.query(sql, paramMap, new AccountRowMapper());
        return accountList;
    }

    public List<TransactionHistory> accountTransactions(Account account) {
        String sql = "SELECT * FROM transaction_history WHERE account_number = :account_number";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("account_number", account.getAccountNumber());
        List<TransactionHistory> transactionHistoryList = jdbcTemplate.query(sql, paramMap, new TransactionRowMapper());
        account.setTransactionHistoryList(transactionHistoryList);
        return transactionHistoryList;
    }

    public List<Account> accountsList() {
        String sql = "SELECT * FROM account";
        List<Account> accountList = jdbcTemplate.query(sql, new HashMap<>(), new AccountRowMapper());
        return accountList;
    }

    public List<Customer> customerList() {
        String sql = "SELECT * FROM customer";
        List<Customer> customerList = jdbcTemplate.query(sql, new HashMap<>(), new CustomerRowMapper());
        return customerList;
    }

    public List<TransactionHistory> transactionHistoryList() {
        String sql = "SELECT * FROM transaction_history";
        List<TransactionHistory> transactionHistoryList = jdbcTemplate.query(sql, new HashMap<>(), new TransactionRowMapper());
        return transactionHistoryList;
    }

    public String findPasswordByUserName(String userName) {
        Map<String, Object> paramMap = new HashMap<>();
        String sql = "SELECT password FROM customer WHERE user_name = :user_name";
        paramMap.put("user_name", userName);
        return jdbcTemplate.queryForObject(sql, paramMap, String.class);
    }

    public void registerCustomer(String firstName, String lastName, String address, String userName, String password){
        Map<String, Object> paramMap = new HashMap<>();
        String sql = "INSERT INTO customer(firstname, lastname, address, user_name, password) VALUES (:firstname, :lastname, :address, :user_name, :password)";
        paramMap.put("firstname", firstName);
        paramMap.put("lastname", lastName);
        paramMap.put("address", address);
        paramMap.put("user_name", userName);
        paramMap.put("password", password);
        jdbcTemplate.update(sql, paramMap);
    }
}




