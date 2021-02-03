package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DataBaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;


public class PersistentTransactionDAO implements TransactionDAO {
    DataBaseHelper dataBaseHelper;
    public PersistentTransactionDAO(Context context) {
        dataBaseHelper = new DataBaseHelper(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        if(accountNo != null){
            dataBaseHelper.addTransaction(transaction);
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return dataBaseHelper.getTransactions();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = dataBaseHelper.getTransactions().size();
        if(size<=limit){
            return dataBaseHelper.getTransactions();
        }
        // return the last <code>limit</code> number of transaction logs
        return dataBaseHelper.getTransaction(10);
    }

}
