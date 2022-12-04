package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private static PersistentAccountDAO persistentAccountDAO = null;
    private Connection connection = null;

    PersistentAccountDAO() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:account.db");

            String createAccountTableSQL = "CREATE TABLE IF NOT EXISTS account " +
                    "(accountNo TEXT PRIMARY KEY, " +
                    "bankName TEXT, " +
                    "accountHolderName TEXT, " +
                    "balance REAL)";

            Statement statement = connection.createStatement();
            statement.execute(createAccountTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PersistentAccountDAO getInstance() {
        if (persistentAccountDAO == null) {
            persistentAccountDAO = new PersistentAccountDAO();
        }
        return persistentAccountDAO;
    }

    @Override
    public List<String> getAccountNumbersList() {
        String getAccountNumbersListSQL = "SELECT accountNo FROM account";
        List<String> accountNumbersList = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getAccountNumbersListSQL);

            while (resultSet.next()) {
                accountNumbersList.add(resultSet.getString("accountNo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        String getAccountsListSQL = "SELECT * FROM account";
        List<Account> accountList = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getAccountsListSQL);

            while (resultSet.next()) {
                accountList.add(new Account(
                        resultSet.getString("accountNo"),
                        resultSet.getString("bankName"),
                        resultSet.getString("accountHolderName"),
                        resultSet.getDouble("balance")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String getAccountSQL = "SELECT * FROM account WHERE accountNo = ?";
        Account acc = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getAccountSQL);
            preparedStatement.setString(1, accountNo);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                acc = new Account(
                        resultSet.getString("accountNo"),
                        resultSet.getString("bankName"),
                        resultSet.getString("accountHolderName"),
                        resultSet.getDouble("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (acc == null) {
            throw new InvalidAccountException("Invalid account number");
        }
        return acc;
    }

    @Override
    public void addAccount(Account account) {
        String addAccountSQL = "INSERT INTO account values (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(addAccountSQL);
            preparedStatement.setString(1, account.getAccountNo());
            preparedStatement.setString(2, account.getBankName());
            preparedStatement.setString(3, account.getAccountHolderName());
            preparedStatement.setDouble(4, account.getBalance());

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String removeAccountSQL = "DELETE FROM account WHERE accountNo = ?";
        boolean isRemoved = false;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(removeAccountSQL);
            preparedStatement.setString(1, accountNo);

            isRemoved = preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!isRemoved) {
            throw new InvalidAccountException("Invalid account number");
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        String updateBalanceSQL = "UPDATE account SET balance = balance ? ? WHERE accountNo = ?";
        boolean isUpdated = false;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateBalanceSQL);
            preparedStatement.setString(1, expenseType == ExpenseType.EXPENSE ? "-" : "+");
            preparedStatement.setDouble(2, amount);
            preparedStatement.setString(3, accountNo);

            isUpdated = preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!isUpdated) {
            throw new InvalidAccountException("Invalid account number");
        }
    }
}
