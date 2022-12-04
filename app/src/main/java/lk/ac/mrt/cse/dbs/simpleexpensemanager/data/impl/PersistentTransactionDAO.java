package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.*;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private static PersistentAccountDAO persistentAccountDAO = null;
    private Connection connection = null;

    private PersistentTransactionDAO() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:transaction.db");

            String createTransactionTableSQL = "CREATE TABLE IF NOT EXISTS transaction " +
                    "(accountNo TEXT, " +
                    "date TEXT, " +
                    "expenseType TEXT, " +
                    "amount REAL)";

            Statement statement = connection.createStatement();
            statement.execute(createTransactionTableSQL);
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
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        String logTransactionSQL = "INSERT INTO transaction VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(logTransactionSQL);
            preparedStatement.setString(1, accountNo);
            preparedStatement.setString(2, date.toString());
            preparedStatement.setString(3, expenseType.toString());
            preparedStatement.setDouble(4, amount);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        String getAllTransactionLogsSQL = "SELECT * FROM transaction";
        List<Transaction> transactionLogs = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getAllTransactionLogsSQL);

            while (resultSet.next()) {
                String accountNo = resultSet.getString("accountNo");
                Date date = new Date(resultSet.getString("date"));
                ExpenseType expenseType = ExpenseType.valueOf(resultSet.getString("expenseType"));
                double amount = resultSet.getDouble("amount");

                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                transactionLogs.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactionLogs;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        String getPaginatedTransactionLogsSQL = "SELECT * FROM transaction LIMIT ?";
        List<Transaction> transactionLogs = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getPaginatedTransactionLogsSQL);
            preparedStatement.setInt(1, limit);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String accountNo = resultSet.getString("accountNo");
                Date date = new Date(resultSet.getString("date"));
                ExpenseType expenseType = ExpenseType.valueOf(resultSet.getString("expenseType"));
                double amount = resultSet.getDouble("amount");

                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                transactionLogs.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactionLogs;
    }
}
