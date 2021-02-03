package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String ACCOUNT_TABLE = "ACCOUNT_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_ACCOUNT_NO = "ACCOUNT_NO";
    public static final String COLUMN_BANK_NAME = "BANK_NAME";
    public static final String COLUMN_ACCOUNT_HOLDER_NAME = "ACCOUNT_HOLDER_NAME";
    public static final String COLUMN_INITIAL_BALANCE = "INITIAL_BALANCE";

    public static final String TRANSACTION_TABLE = "TRANSACTION_TABLE";
    public static final String COLUMN_ID_1 = "ID1";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_ACCOUNT_NUM = "ACCOUNT_NUM";
    public static final String COLUMN_EXPENSE_TYPE = "EXPENSE_TYPE";
    public static final String COLUMN_AMOUNT = "AMOUNT";

    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH);

    public DataBaseHelper(@Nullable Context context) {
        super(context, "180126P.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createAccountTableStatement = "CREATE TABLE " + ACCOUNT_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ACCOUNT_NO + " TEXT, " + COLUMN_BANK_NAME + " TEXT, " + COLUMN_ACCOUNT_HOLDER_NAME + " TEXT," + COLUMN_INITIAL_BALANCE + " DOUBLE)";
        String createTransactionTableStatement = "CREATE TABLE " + TRANSACTION_TABLE + "(" + COLUMN_ID_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DATE + " TEXT, " + COLUMN_ACCOUNT_NUM + " TEXT, " + COLUMN_EXPENSE_TYPE + " TEXT, " + COLUMN_AMOUNT + " DOUBLE)";

        sqLiteDatabase.execSQL(createAccountTableStatement);
        sqLiteDatabase.execSQL(createTransactionTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String drop_account_query = "DROP TABLE IF EXISTS "+ ACCOUNT_TABLE;
        String drop_transaction_query = "DROP TABLE IF EXISTS "+ TRANSACTION_TABLE;
        sqLiteDatabase.execSQL(drop_account_query);
        sqLiteDatabase.execSQL(drop_transaction_query);
        onCreate(sqLiteDatabase);
    }

    public void addAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_ACCOUNT_NO,account.getAccountNo());
        contentValues.put(COLUMN_BANK_NAME,account.getBankName());
        contentValues.put(COLUMN_ACCOUNT_HOLDER_NAME,account.getAccountHolderName());
        contentValues.put(COLUMN_INITIAL_BALANCE,account.getBalance());

        db.insert(ACCOUNT_TABLE,null,contentValues);
        db.close();
    }

    public Account getAccount(String accNo){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + ACCOUNT_TABLE + " WHERE " + COLUMN_ACCOUNT_NO + " = ?";
        Cursor cursor = db.rawQuery(query,new String[]{accNo});
        Account account = null;
        if(cursor.getCount() == 0){
            return null;
        }else{
            while(cursor.moveToNext()){
                String accountNo = cursor.getString(1);
                String bankName = cursor.getString(2);
                String accountHolderName = cursor.getString(3);
                Double balance = cursor.getDouble(4);
                account = new Account(accountNo,bankName,accountHolderName,balance);
            }
            cursor.close();
            db.close();
            return account;
        }

    }

    public ArrayList<Account> getAccountsList(){
        ArrayList<Account> accountList = new ArrayList<>();
        String query = "SELECT * FROM "+ ACCOUNT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                String accountNo = cursor.getString(1);
                String bankName = cursor.getString(2);
                String accountHolderName = cursor.getString(3);
                Double balance = cursor.getDouble(4);
                Account account = new Account(accountNo,bankName,accountHolderName,balance);
                accountList.add(account);

            }while(cursor.moveToNext());
        }else{
            return accountList;
        }
        cursor.close();
        db.close();
        return accountList;
    }

    public boolean updateBalance(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ACCOUNT_NO,account.getAccountNo());
        contentValues.put(COLUMN_BANK_NAME,account.getBankName());
        contentValues.put(COLUMN_ACCOUNT_HOLDER_NAME,account.getAccountHolderName());
        contentValues.put(COLUMN_INITIAL_BALANCE,account.getBalance());

        long value = db.update(ACCOUNT_TABLE,contentValues,COLUMN_ACCOUNT_NO+"= ?",new String[]{account.getAccountNo()});
        if(value == -1){
            return false;
        }else{
            return true;
        }

    }

    public boolean removeAccount(String accountNo){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + ACCOUNT_TABLE + "WHERE" + COLUMN_ACCOUNT_NO + " = " + accountNo;
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }

    public void addTransaction(Transaction transaction){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_DATE,dateFormat.format(transaction.getDate()));
        contentValues.put(COLUMN_ACCOUNT_NUM,transaction.getAccountNo());
        contentValues.put(COLUMN_EXPENSE_TYPE,transaction.getExpenseType().toString());
        contentValues.put(COLUMN_AMOUNT,transaction.getAmount());

        db.insert(TRANSACTION_TABLE,null,contentValues);
        db.close();
    }

    public ArrayList<Transaction> getTransactions(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+ TRANSACTION_TABLE;
        Cursor cursor = db.rawQuery(query,null);
        return getAllTransactions(cursor);
    }

    public ArrayList<Transaction> getTransaction(int limit){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+ TRANSACTION_TABLE + " ORDER BY " +  COLUMN_ID_1 +"  DESC LIMIT "+limit;
        Cursor cursor = db.rawQuery(query,null);
        return getAllTransactions(cursor);
    }

    public ArrayList<Transaction> getAllTransactions(Cursor cursor){
        ArrayList<Transaction> transactions = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
        if(cursor.getCount() == 0){
            return transactions;
        }else{
            while(cursor.moveToNext()){
                Date date = new Date();
                try{
                    date = dateFormat.parse(cursor.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String accountNo = cursor.getString(2);
                ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(3));
                Double amount = cursor.getDouble(4);
                Transaction transaction = new Transaction(date,accountNo,expenseType,amount);
                transactions.add(transaction);
            }return transactions;
        }
    }

}
