package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    private static PersistentTransactionDAO persistentTransactionDAO;

    // Singleton pattern
    private PersistentTransactionDAO(Context context) {
        super(context, "expenseManager.db", null, 3);
        onCreate(this.getWritableDatabase());
    }

    public static PersistentTransactionDAO getInstance(Context context) {
        if (persistentTransactionDAO == null) {
            persistentTransactionDAO = new PersistentTransactionDAO(context);
        }
        return persistentTransactionDAO;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("SimpleDateFormat") String dateFormat = new SimpleDateFormat("yyyy-MM-dd").format(date);
        db.execSQL("INSERT INTO trans (accountNo, date, expenseType, amount) VALUES (?, ?, ?, ?)",
                new Object[]{accountNo, dateFormat, ((expenseType == ExpenseType.EXPENSE) ? 1: 0), amount});
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM trans", null);

        List<Transaction> transactions = new ArrayList<>();
        while (cursor.moveToNext()) {
            @SuppressLint("SimpleDateFormat") Transaction transaction = null;
            try {
                transaction = new Transaction(
                        new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(1)),
                        cursor.getString(0),
                        (((cursor.getInt(2) == 1) ? ExpenseType.EXPENSE : ExpenseType.INCOME)),
                        cursor.getDouble(3)
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            transactions.add(transaction);
        }
        return transactions;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM trans LIMIT " + limit + ";", null);

        List<Transaction> transactions = new ArrayList<>();
        while (cursor.moveToNext()) {
            @SuppressLint("SimpleDateFormat") Transaction transaction = null;
            try {
                transaction = new Transaction(
                        new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(1)),
                        cursor.getString(0),
                        (((cursor.getInt(2) == 1) ? ExpenseType.EXPENSE : ExpenseType.INCOME)),
                        cursor.getDouble(3)
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            transactions.add(transaction);
        }
        return transactions;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTransactionTableSQL = "CREATE TABLE IF NOT EXISTS trans " +
                "(accountNo TEXT, " +
                "date TEXT, " +
                "expenseType INT, " +
                "amount REAL)";

        sqLiteDatabase.execSQL(createTransactionTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS trans");
        onCreate(sqLiteDatabase);
    }
}
