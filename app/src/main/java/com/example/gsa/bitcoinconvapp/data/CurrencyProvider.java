package com.example.gsa.bitcoinconvapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.gsa.bitcoinconvapp.data.CurrencyContract;
import com.example.gsa.bitcoinconvapp.data.CurrencyContract.CurrencyEntry;
import com.example.gsa.bitcoinconvapp.data.CurrencyDbHelper;
import com.example.gsa.bitcoinconvapp.data.CurrencyContract.WatchlistEntry;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by GSA on 10/20/2017.
 * Handles the whole CRUD operation on the two database created
 */
public class CurrencyProvider extends ContentProvider {
    private static  final int CURRENCIES = 100;
    private static final int CURRENCY_ID = 101;
    private static final int WATCHLIST = 200;
    private static final int WATCHLIST_ID = 201;
    private String TABlENAME;

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private CurrencyDbHelper mCurrencyDbHelper;
    static {
            sUriMatcher.addURI(CurrencyContract.CONTENT_AUTHORITY,CurrencyContract.PATH_CURRENCY,CURRENCIES);
            sUriMatcher.addURI(CurrencyContract.CONTENT_AUTHORITY,CurrencyContract.PATH_CURRENCY+"/#",CURRENCY_ID);
            sUriMatcher.addURI(CurrencyContract.CONTENT_AUTHORITY,CurrencyContract.PATH_WATCHLIST,WATCHLIST);
            sUriMatcher.addURI(CurrencyContract.CONTENT_AUTHORITY,CurrencyContract.PATH_WATCHLIST+"/#",WATCHLIST_ID);
    }

    @Override
    public boolean onCreate() {
        mCurrencyDbHelper = new CurrencyDbHelper(getContext());
        return true;
    }

    // queries the database for data
    // returns  a cursor which points to the data in the database.
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mCurrencyDbHelper.getReadableDatabase();
        Cursor cursor;
        determineTableName(uri);
        int match = sUriMatcher.match(uri);
        switch (match){
            case CURRENCIES:
            case WATCHLIST:
                cursor = database.query(TABlENAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case CURRENCY_ID:
            case WATCHLIST_ID:
                if (match == CURRENCY_ID) {
                    selection= CurrencyEntry._ID+"=?"; }
                else {  selection = WatchlistEntry._ID + "=?";}
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TABlENAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case CURRENCIES:
                return CurrencyEntry.CONTENT_LIST_TYPE;
            case CURRENCY_ID:
                return CurrencyEntry.CONTENT_ITEM_TYPE;
            case WATCHLIST:
                return WatchlistEntry.CONTENT_LIST_TYPE;
            case WATCHLIST_ID:
                return WatchlistEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + "with match " + match);
        }

    }

    // insert data to the database
    // returns the uri of the inserted row
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CURRENCIES:
            case WATCHLIST:
                return insertCurrency(uri,values);
            default:
                throw new IllegalArgumentException("Insertion isn't supported for " + uri);
        }
    }

    // deletes data from the database
    // returns the number of rows deleted in the database
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mCurrencyDbHelper.getWritableDatabase();
        int rowsDeleted;
        determineTableName(uri);
        final int match = sUriMatcher.match(uri);
        switch (match){
            case CURRENCIES:
            case WATCHLIST:
                rowsDeleted = database.delete(TABlENAME,selection,selectionArgs);
                break;
            case CURRENCY_ID:
            case WATCHLIST_ID:
                if (match == CURRENCY_ID) {
                    selection= CurrencyEntry._ID+"=?"; }
                else {  selection = WatchlistEntry._ID + "=?";}
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted=database.delete(TABlENAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted ;
    }

    // updates the already present data in the database
    // returns the number of rows updated in the database
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case CURRENCIES:
            case WATCHLIST:
                return updateCurrency(uri,values,selection,selectionArgs);
            case CURRENCY_ID:
            case WATCHLIST_ID:
                if (match == CURRENCY_ID) {
                selection= CurrencyEntry._ID+"=?"; }
                else {  selection = WatchlistEntry._ID + "=?";}
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateCurrency(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    // Exact method which inserts to the database
    public Uri insertCurrency(Uri uri, ContentValues values) {
        determineTableName(uri);
        SQLiteDatabase database = mCurrencyDbHelper.getWritableDatabase();
        long id = database.insert(TABlENAME,null,values);
        if (id == -1){
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
     }
    // Exact method which updates the database
    public int updateCurrency(Uri uri,ContentValues values,String selection, String[] selectionArgs) {
        determineTableName(uri);
        String btcPer = values.getAsString(CurrencyEntry.CURR_BTC_PER);

        SQLiteDatabase database = mCurrencyDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(TABlENAME,values,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }

    // determine the name of the table on which to perform any of the CRUD operation using
    // the passed uri.
     private void determineTableName(Uri uri) {
         int match = sUriMatcher.match(uri);
         if (match == CURRENCIES || match == CURRENCY_ID) {
             TABlENAME = CurrencyEntry.TABLE_NAME ;
         }
         else if (match == WATCHLIST || match == WATCHLIST_ID) {
             TABlENAME = WatchlistEntry.TABLE_NAME;
         }
         else {
             throw new IllegalArgumentException("Invalid URI " + uri);
         }
     }
}
