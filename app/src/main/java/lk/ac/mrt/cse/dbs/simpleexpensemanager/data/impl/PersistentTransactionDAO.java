package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    private static final String TABLE_NAME = "trans";
    private static final String ACCOUNT_NO = "accountNo";
    private static final String DATE = "date";
    private static final String EXPENSE_TYPE = "expenseType";
    private static final String AMOUNT = "amount";
    private static final String ID = "id";
    private static final int VERSION = 1;
    private static PersistentTransactionDAO persistentTransactionDAO;

    // Singleton pattern
    private PersistentTransactionDAO(Context context) {
        super(context, "200517U.db", null, VERSION);
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

        ContentValues contentValues = new ContentValues();

        contentValues.put(ACCOUNT_NO, accountNo);
        contentValues.put(DATE, dateFormat);
        contentValues.put(EXPENSE_TYPE, ((expenseType == ExpenseType.EXPENSE) ? 1 : 0));
        contentValues.put(AMOUNT, amount);

        db.insert(TABLE_NAME, null, contentValues);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
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
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " ORDER BY " + ID + " DESC " +
                " LIMIT " + limit + ";", null);

        List<Transaction> transactions = new ArrayList<>();
        while (cursor.moveToNext()) {
            @SuppressLint("SimpleDateFormat") Transaction transaction = null;
            try {
                transaction = new Transaction(
                        new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(2)),
                        cursor.getString(1),
                        (((cursor.getInt(3) == 1) ? ExpenseType.EXPENSE : ExpenseType.INCOME)),
                        cursor.getDouble(4)
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
        String createTransactionTableSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " " +
                "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ACCOUNT_NO + " TEXT, " +
                DATE + " TEXT, " +
                EXPENSE_TYPE + " INT, " +
                AMOUNT + " REAL)";

        sqLiteDatabase.execSQL(createTransactionTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
