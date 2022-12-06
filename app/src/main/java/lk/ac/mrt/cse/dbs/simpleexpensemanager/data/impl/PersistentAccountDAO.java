package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    private static final String TABLE_NAME = "account";
    private static final String ACCOUNT_NO = TABLE_NAME + "No";
    private static final String BANK_NAME = "bankName";
    private static final String ACCOUNT_HOLDER_NAME = TABLE_NAME + "HolderName";
    private static final String BALANCE = "balance";
    private static PersistentAccountDAO persistentAccountDAO;

    PersistentAccountDAO(Context context) {
        super(context, "200517U.db", null, 3);
        onCreate(this.getWritableDatabase());
    }

    public static PersistentAccountDAO getInstance(Context context) {
        if (persistentAccountDAO == null) {
            persistentAccountDAO = new PersistentAccountDAO(context);
        }
        return persistentAccountDAO;
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT " + ACCOUNT_NO + " FROM " + TABLE_NAME, null);

        List<String> accountNumbers = new ArrayList<>();
        while (cursor.moveToNext()) {
            accountNumbers.add(cursor.getString(0));
        }
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        List<Account> accounts = new ArrayList<>();
        while (cursor.moveToNext()) {
            Account account = new Account(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3)
            );
            accounts.add(account);
        }
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ACCOUNT_NO + " = ?", new String[]{accountNo});

        if (cursor.getCount() == 0) {
            throw new InvalidAccountException("Account " + accountNo + " does not exist.");
        }
        cursor.moveToFirst();

        return new Account(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getDouble(3)
        );
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?)", new String[]{
                account.getAccountNo(),
                account.getBankName(),
                account.getAccountHolderName(),
                String.valueOf(account.getBalance())
        });
    }

    @Override
    public void removeAccount(String accountNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ACCOUNT_NO + " = ?", new String[]{accountNo});
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account = getAccount(accountNo);
        double newBalance = expenseType == ExpenseType.EXPENSE ? account.getBalance() - amount : account.getBalance() + amount;



        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET " + BALANCE + " = ? WHERE " + ACCOUNT_NO + " = ?", new String[]{
                String.valueOf(newBalance),
                accountNo
        });
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createAccountTableSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " " +
                "(" + ACCOUNT_NO + " TEXT PRIMARY KEY, " +
                BANK_NAME + " TEXT, " +
                ACCOUNT_HOLDER_NAME + " TEXT, " +
                BALANCE + " REAL)";

        sqLiteDatabase.execSQL(createAccountTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
