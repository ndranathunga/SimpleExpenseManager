package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class PersistentExpenseManager extends ExpenseManager {
    Context context;

    public PersistentExpenseManager(Context context) {
        this.context = context;
        setup();
    }

    @Override
    public void setup() {
        PersistentAccountDAO persistentAccountDAO = PersistentAccountDAO.getInstance(context);
        setAccountsDAO(persistentAccountDAO);

        PersistentTransactionDAO persistentTransactionDAO = PersistentTransactionDAO.getInstance(context);
        setTransactionsDAO(persistentTransactionDAO);
    }
}
