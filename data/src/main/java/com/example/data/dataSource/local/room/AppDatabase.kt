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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal

@Database(
    entities = [Category::class, Transaction::class, Wallet::class, WalletGroup::class],
    version = 1,
    exportSchema = false
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
                ).fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                fillWithStartingData(context, getInstance(context))
                            }
                        }
                    }).build()
                INSTANCE = instance
                instance
            }
        }

        private fun fillWithStartingData(context: Context, database: AppDatabase) {
            try {
                val dataMap = mapOf(
                    R.raw.category to Pair(CategoryColumn.TABLE_NAME) { obj: JSONObject ->
                        Category(
                            id = obj.getInt(CategoryColumn.COLUMN_ID),
                            name = obj.getString(CategoryColumn.COLUMN_NAME),
                            type = CategoryType.valueOf(obj.getString(CategoryColumn.COLUMN_TYPE))
                        )
                    },
                    R.raw.wallet_group to Pair(WalletGroupColumn.TABLE_NAME) { obj: JSONObject ->
                        WalletGroup(
                            id = obj.getInt(WalletGroupColumn.COLUMN_ID),
                            name = obj.getString(WalletGroupColumn.COLUMN_NAME)
                        )
                    },
                    R.raw.wallet to Pair(WalletColumn.TABLE_NAME) { obj: JSONObject ->
                        Wallet(
                            id = obj.getInt(WalletColumn.COLUMN_ID),
                            name = obj.getString(WalletColumn.COLUMN_NAME),
                            balance = BigDecimal(obj.getString(WalletColumn.COLUMN_BALANCE)),
                            walletGroupId = obj.getInt(WalletColumn.COLUMN_WALLET_GROUP_ID)
                        )
                    },
                    R.raw.transaction to Pair(TransactionColumn.TABLE_NAME) { obj: JSONObject ->
                        Transaction(
                            id = obj.getInt(TransactionColumn.COLUMN_ID),
                            dateTime = obj.getString(TransactionColumn.COLUMN_DATE_TIME),
                            type = TransactionType.valueOf(obj.getString(TransactionColumn.COLUMN_TYPE)),
                            categoryId = obj.optIntOrNull(TransactionColumn.COLUMN_CATEGORY_ID),
                            fromWalletId = obj.optInt(TransactionColumn.COLUMN_FROM_WALLET_ID),
                            toWalletId = obj.optIntOrNull(TransactionColumn.COLUMN_TO_WALLET_ID),
                            amount = BigDecimal(obj.getString(TransactionColumn.COLUMN_AMOUNT)),
                            transferFee = BigDecimal(obj.getString(TransactionColumn.COLUMN_TRANSFER_FEE)),
                            note = obj.optStringOrNull(TransactionColumn.COLUMN_NOTE),
                            receipt = obj.optJSONArray(TransactionColumn.COLUMN_RECEIPT)?.toByteArray()
                        )
                    }
                )

                dataMap.forEach { (jsonFile, pair) ->
                    loadJsonArray(context, jsonFile, pair.first)?.toList(pair.second)?.let {
                        insertData(database, pair.first, it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun insertData(database: AppDatabase, tableName: String, data: List<Any>) {
            when (tableName) {
                CategoryColumn.TABLE_NAME -> database.categoryDao().insertCategories(data as List<Category>)
                WalletGroupColumn.TABLE_NAME -> database.walletGroupDao().insertWalletGroups(data as List<WalletGroup>)
                WalletColumn.TABLE_NAME -> database.walletDao().insertWallets(data as List<Wallet>)
                TransactionColumn.TABLE_NAME -> database.transactionDao().insertTransactions(data as List<Transaction>)
            }
        }

        private fun loadJsonArray(context: Context, jsonRaw: Int, key: String): JSONArray? =
            try {
                context.resources.openRawResource(jsonRaw).bufferedReader().use {
                    JSONObject(it.readText()).optJSONArray(key)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        private fun JSONArray.toList(transform: (JSONObject) -> Any): List<Any> =
            List(length()) { index -> transform(getJSONObject(index)) }

        private fun JSONArray.toByteArray(): ByteArray =
            ByteArray(length()) { index -> getInt(index).toByte() }

        private fun JSONObject.optIntOrNull(key: String): Int? =
            opt(key)?.let { if (it is Int) it else null }

        private fun JSONObject.optStringOrNull(key: String): String? =
            opt(key)?.let { if (it is String) it else null }
    }
}