package com.example.myfarm2024.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FieldDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "FieldDatabaseV4.db"
        private const val TABLE_FIELDS = "fields"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PARCEL_ID = "parcelId"
        private const val COLUMN_PARCEL_NUMBER = "parcelNumber"
        private const val COLUMN_SURFACE_AREA = "surfaceArea"
        private const val COLUMN_PROVINCE = "province"
        private const val COLUMN_COUNTY = "county"
        private const val COLUMN_COMMUNE = "commune"
        private const val COLUMN_TOWN = "town"
        private const val COLUMN_LAND_CLASS = "landClass"

        // Nowa tabela dla konta i wydatków
        private const val TABLE_ACCOUNT = "account"
        private const val COLUMN_ACCOUNT_ID = "accountId"
        private const val COLUMN_ACCOUNT_NAME = "accountName"
        private const val COLUMN_ACCOUNT_TYPE = "accountType"
        private const val COLUMN_CURRENT_BALANCE = "currentBalance"

        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_EXPENSE_ID = "expenseId"
        private const val COLUMN_EXPENSE_AMOUNT = "amount"
        private const val COLUMN_EXPENSE_CATEGORY = "category"
        private const val COLUMN_EXPENSE_SUBCATEGORY = "subcategory"
        private const val COLUMN_EXPENSE_DATE = "date"
        private const val COLUMN_ACCOUNT_ID_EXPENSE = "accountId" // Nowa kolumna

        // Tabela kategorii i podkategorii wydatków
        private const val TABLE_EXPENSE_CATEGORIES = "expense_categories"
        private const val COLUMN_CATEGORY_ID = "categoryId"
        private const val COLUMN_CATEGORY_NAME = "categoryName"
        private const val COLUMN_SUBCATEGORY_NAME = "subcategoryName"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_FIELDS_TABLE = ("CREATE TABLE $TABLE_FIELDS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY," +
                "$COLUMN_PARCEL_ID TEXT," +
                "$COLUMN_PARCEL_NUMBER TEXT," +
                "$COLUMN_SURFACE_AREA REAL," +
                "$COLUMN_PROVINCE TEXT," +
                "$COLUMN_COUNTY TEXT," +
                "$COLUMN_COMMUNE TEXT," +
                "$COLUMN_TOWN TEXT," +
                "$COLUMN_LAND_CLASS TEXT)")

        val CREATE_ACCOUNT_TABLE = ("CREATE TABLE $TABLE_ACCOUNT (" +
                "$COLUMN_ACCOUNT_ID INTEGER PRIMARY KEY," +
                "$COLUMN_ACCOUNT_NAME TEXT," +
                "$COLUMN_ACCOUNT_TYPE TEXT," +
                "$COLUMN_CURRENT_BALANCE REAL)")

        val CREATE_EXPENSES_TABLE = ("CREATE TABLE $TABLE_EXPENSES (" +
                "$COLUMN_EXPENSE_ID INTEGER PRIMARY KEY," +
                "$COLUMN_EXPENSE_AMOUNT REAL," +
                "$COLUMN_EXPENSE_CATEGORY TEXT," +
                "$COLUMN_EXPENSE_SUBCATEGORY TEXT," +
                "$COLUMN_EXPENSE_DATE TEXT," +
                "$COLUMN_ACCOUNT_ID_EXPENSE INTEGER," + // Nowa kolumna
                "FOREIGN KEY($COLUMN_ACCOUNT_ID_EXPENSE) REFERENCES $TABLE_ACCOUNT($COLUMN_ACCOUNT_ID))")

        val CREATE_EXPENSE_CATEGORIES_TABLE = ("CREATE TABLE $TABLE_EXPENSE_CATEGORIES (" +
                "$COLUMN_CATEGORY_ID INTEGER PRIMARY KEY," +
                "$COLUMN_CATEGORY_NAME TEXT," +
                "$COLUMN_SUBCATEGORY_NAME TEXT)")

        db.execSQL(CREATE_FIELDS_TABLE)
        db.execSQL(CREATE_ACCOUNT_TABLE)
        db.execSQL(CREATE_EXPENSES_TABLE)
        db.execSQL(CREATE_EXPENSE_CATEGORIES_TABLE)

        // Dodajemy przykładowe kategorie wydatków
        addDefaultExpenseCategories(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FIELDS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSE_CATEGORIES")
        onCreate(db)
    }

    fun addField(fieldData: FieldData) {
        val values = ContentValues()
        values.put(COLUMN_PARCEL_ID, fieldData.parcelId)
        values.put(COLUMN_PARCEL_NUMBER, fieldData.parcelNumber)
        values.put(COLUMN_SURFACE_AREA, fieldData.surfaceArea)
        values.put(COLUMN_PROVINCE, fieldData.province)
        values.put(COLUMN_COUNTY, fieldData.county)
        values.put(COLUMN_COMMUNE, fieldData.commune)
        values.put(COLUMN_TOWN, fieldData.town)
        values.put(COLUMN_LAND_CLASS, fieldData.landClass)

        val db = this.writableDatabase
        db.insert(TABLE_FIELDS, null, values)
    }

    fun addExpense(amount: Double, category: String, subcategory: String, date: String, accountId: Int) {
        val values = ContentValues()
        values.put(COLUMN_EXPENSE_AMOUNT, amount)
        values.put(COLUMN_EXPENSE_CATEGORY, category)
        values.put(COLUMN_EXPENSE_SUBCATEGORY, subcategory)
        values.put(COLUMN_EXPENSE_DATE, date)
        values.put(COLUMN_ACCOUNT_ID_EXPENSE, accountId) // Przypisanie accountId

        val db = this.writableDatabase
        db.insert(TABLE_EXPENSES, null, values)

        // Update current balance
        val currentBalance = getAccountBalance(accountId)
        val newBalance = currentBalance - amount
        val updateValues = ContentValues()
        updateValues.put(COLUMN_CURRENT_BALANCE, newBalance)
        db.update(TABLE_ACCOUNT, updateValues, "$COLUMN_ACCOUNT_ID = ?", arrayOf(accountId.toString()))
    }

    @SuppressLint("Range")
    fun getAllExpenseCategories(): List<Pair<String, String?>> {
        val categories = mutableListOf<Pair<String, String?>>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_EXPENSE_CATEGORIES"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val categoryName = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME))
                val subcategoryName = cursor.getString(cursor.getColumnIndex(COLUMN_SUBCATEGORY_NAME))
                categories.add(Pair(categoryName, if (subcategoryName.isEmpty()) null else subcategoryName))
            } while (cursor.moveToNext())
        }

        cursor.close()

        return categories
    }

    private fun addDefaultExpenseCategories(db: SQLiteDatabase) {
        val categories = arrayOf(
            Pair("Jedzenie", null),
            Pair("Transport", null),
            Pair("Zakupy", null),
            Pair("Rachunki", null),
            Pair("Zdrowie", null),
            Pair("Rozrywka", null),
            Pair("Inne", null)
        )

        for (category in categories) {
            val values = ContentValues()
            values.put(COLUMN_CATEGORY_NAME, category.first)
            values.put(COLUMN_SUBCATEGORY_NAME, category.second ?: "")
            db.insert(TABLE_EXPENSE_CATEGORIES, null, values)
        }
    }

    @SuppressLint("Range")
    fun getCurrentBalance(): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_CURRENT_BALANCE FROM $TABLE_ACCOUNT WHERE $COLUMN_ACCOUNT_ID = 1", null)
        var currentBalance = 0.0

        if (cursor.moveToFirst()) {
            currentBalance = cursor.getDouble(cursor.getColumnIndex(COLUMN_CURRENT_BALANCE))
        }

        cursor.close()

        return currentBalance
    }

    @SuppressLint("Range")
    fun getAccountBalance(accountId: Int): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_CURRENT_BALANCE FROM $TABLE_ACCOUNT WHERE $COLUMN_ACCOUNT_ID = ?", arrayOf(accountId.toString()))
        var currentBalance = 0.0

        if (cursor.moveToFirst()) {
            currentBalance = cursor.getDouble(cursor.getColumnIndex(COLUMN_CURRENT_BALANCE))
        }

        cursor.close()

        return currentBalance
    }

    fun addExpenseSubcategory(categoryName: String, subcategoryName: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CATEGORY_NAME, categoryName)
            put(COLUMN_SUBCATEGORY_NAME, subcategoryName)
        }
        db.insert(TABLE_EXPENSE_CATEGORIES, null, values)
    }

    fun addAccount(accountName: String, accountType: String, initialBalance: Double = 0.0) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACCOUNT_NAME, accountName)
            put(COLUMN_ACCOUNT_TYPE, accountType)
            put(COLUMN_CURRENT_BALANCE, initialBalance)
        }
        db.insert(TABLE_ACCOUNT, null, values)
    }

    @SuppressLint("Range")
    fun getAllAccounts(): List<Pair<Int, String>> {
        val accounts = mutableListOf<Pair<Int, String>>()

        val db = this.readableDatabase
        val query = "SELECT $COLUMN_ACCOUNT_ID, $COLUMN_ACCOUNT_NAME FROM $TABLE_ACCOUNT"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val accountId = cursor.getInt(cursor.getColumnIndex(COLUMN_ACCOUNT_ID))
                val accountName = cursor.getString(cursor.getColumnIndex(COLUMN_ACCOUNT_NAME))
                accounts.add(Pair(accountId, accountName))
            } while (cursor.moveToNext())
        }

        cursor.close()

        return accounts
    }
}
