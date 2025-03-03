package com.example.data.dataSource.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.R
import com.example.data.dataSource.local.room.dao.CategoryDao
import com.example.data.dataSource.local.room.dao.TransactionDao
import com.example.data.dataSource.local.room.dao.WalletDao
import com.example.data.dataSource.local.room.dao.WalletGroupDao
import com.example.data.dataSource.local.room.entity.Category
import com.example.data.dataSource.local.room.entity.Transaction
import com.example.data.dataSource.local.room.entity.Wallet
import com.example.data.dataSource.local.room.entity.WalletGroup
import com.example.domain.constant.CategoryType
import com.example.domain.constant.TransactionType
import com.example.domain.constant.tableColumn.CategoryColumn
import com.example.domain.constant.tableColumn.TransactionColumn
import com.example.domain.constant.tableColumn.WalletColumn
import com.example.domain.constant.tableColumn.WalletGroupColumn
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.Executors

@Database(
    entities = [Category::class, Transaction::class, Wallet::class, WalletGroup::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun walletDao(): WalletDao
    abstract fun walletGroupDao(): WalletGroupDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java,
                    "moi-catatan-keuangan.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let {
                            Executors.newSingleThreadExecutor().execute {
                                fillWithStartingData(context)
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private fun fillWithStartingData(context: Context) {
            try {
                // Load category data from JSON files and insert into the database
                loadJsonArray(
                    context,
                    R.raw.category,
                    CategoryColumn.TABLE_NAME
                )?.let { jsonArray ->
                    jsonArray.toList {
                        Category(
                            id = it.getInt(CategoryColumn.COLUMN_ID),
                            name = it.getString(CategoryColumn.COLUMN_NAME),
                            type = CategoryType.valueOf(it.getString(CategoryColumn.COLUMN_TYPE).uppercase())
                        )
                    }.also { INSTANCE?.categoryDao()?.insertCategories(it as List<Category>) }
                }

                // Load wallet group data from JSON files and insert into the database
                loadJsonArray(
                    context,
                    R.raw.wallet_group,
                    WalletGroupColumn.TABLE_NAME
                )?.let { jsonArray ->
                    jsonArray.toList {
                        WalletGroup(
                            id = it.getInt(WalletGroupColumn.COLUMN_ID),
                            name = it.getString(WalletGroupColumn.COLUMN_NAME)
                        )
                    }.also { INSTANCE?.walletGroupDao()?.insertWalletGroups(it as List<WalletGroup>) }
                }

                // Load wallet data from JSON files and insert into the database
                loadJsonArray(context, R.raw.wallet, WalletColumn.TABLE_NAME)?.let { jsonArray ->
                    jsonArray.toList {
                        Wallet(
                            id = it.getInt(WalletColumn.COLUMN_ID),
                            name = it.getString(WalletColumn.COLUMN_NAME),
                            balance = it.getString(WalletColumn.COLUMN_BALANCE).toBigDecimal(),
                            walletGroupId = it.getInt(WalletColumn.COLUMN_WALLET_GROUP_ID)
                        )
                    }.also { INSTANCE?.walletDao()?.insertWallets(it as List<Wallet>) }
                }

                // Load transaction data from JSON files and insert into the database
                loadJsonArray(
                    context,
                    R.raw.transaction,
                    TransactionColumn.TABLE_NAME
                )?.let { jsonArray ->
                    jsonArray.toList {
                        Transaction(
                            id = it.getInt(TransactionColumn.COLUMN_ID),
                            dateTime = it.getString(TransactionColumn.COLUMN_DATE_TIME),
                            type = TransactionType.valueOf(
                                it.getString(TransactionColumn.COLUMN_TYPE).uppercase()
                            ),
                            categoryId = it.optInt(TransactionColumn.COLUMN_CATEGORY_ID, 0),
                            fromWalletId = it.optInt(TransactionColumn.COLUMN_FROM_WALLET_ID, 0),
                            toWalletId = it.optInt(TransactionColumn.COLUMN_TO_WALLET_ID, 0),
                            amount = it.getString(TransactionColumn.COLUMN_AMOUNT).toBigDecimal(),
                            transferFee = it.getString(TransactionColumn.COLUMN_TRANSFER_FEE).toBigDecimal(),
                            note = it.optString(TransactionColumn.COLUMN_NOTE, null),
                            receipt = it.optJSONArray(TransactionColumn.COLUMN_RECEIPT)?.toByteArray() ?: byteArrayOf()
                        )
                    }.also { INSTANCE?.transactionDao()?.insertTransactions(it as List<Transaction>) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun loadJsonArray(context: Context, jsonRaw: Int, key: String): JSONArray? {
            return try {
                context.resources.openRawResource(jsonRaw).bufferedReader().use { reader ->
                    JSONObject(reader.readText()).optJSONArray(key)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        private fun JSONArray.toList(transform: (JSONObject) -> Any): List<Any> {
            return List(length()) { index -> transform(getJSONObject(index)) }
        }

        private fun JSONArray.toByteArray(): ByteArray {
            return ByteArray(length()) { index -> getInt(index).toByte() }
        }
    }
}