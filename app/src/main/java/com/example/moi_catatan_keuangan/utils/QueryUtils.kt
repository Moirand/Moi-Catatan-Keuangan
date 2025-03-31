package com.example.moi_catatan_keuangan.utils

import android.annotation.SuppressLint
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.domain.constant.TransactionType
import com.example.domain.constant.tableColumn.TransactionColumn

@SuppressLint("DefaultLocale")
object QueryUtils {
    fun getTransactionsByMonthYear(month: Int, year: Int): SimpleSQLiteQuery =
        SimpleSQLiteQuery(
            """
            SELECT *
            FROM ${TransactionColumn.TABLE_NAME}
            WHERE ${TransactionColumn.COLUMN_DATE_TIME}
            LIKE '%${String.format("%02d", month)}-${year}%'
            ORDER BY ${TransactionColumn.COLUMN_DATE_TIME} DESC
            """.trimMargin()
        )

    fun calculateIncomeAmounts(month: Int, year: Int): SimpleSQLiteQuery =
        SimpleSQLiteQuery(
            """
            SELECT SUM(${TransactionColumn.COLUMN_AMOUNT})
            FROM ${TransactionColumn.TABLE_NAME}
            WHERE ${TransactionColumn.COLUMN_DATE_TIME}
            LIKE '%${String.format("%02d", month)}-${year}%'
            AND ${TransactionColumn.COLUMN_TYPE} = '${TransactionType.Income}'
            """.trimIndent()
        )

    fun calculateExpenseAmounts(month: Int, year: Int): SimpleSQLiteQuery =
        SimpleSQLiteQuery(
            """
            SELECT (IFNULL(expense_amounts, 0) + IFNULL(transfer_fee, 0)) AS total_expense
            FROM (
                SELECT
                    (
                        SELECT SUM(${TransactionColumn.COLUMN_AMOUNT})
                        FROM ${TransactionColumn.TABLE_NAME}
                        WHERE ${TransactionColumn.COLUMN_DATE_TIME}
                        LIKE '%${String.format("%02d", month)}-${year}%'
                        AND ${TransactionColumn.COLUMN_TYPE} = '${TransactionType.Expense}'
                    ) AS expense_amounts,
                    
                    (
                        SELECT SUM(${TransactionColumn.COLUMN_TRANSFER_FEE})
                        FROM ${TransactionColumn.TABLE_NAME}
                        WHERE ${TransactionColumn.COLUMN_DATE_TIME}
                        LIKE '%${String.format("%02d", month)}-${year}%'
                        AND ${TransactionColumn.COLUMN_TYPE} = '${TransactionType.Transfer}'
                    ) AS transfer_fee
                )
            """.trimIndent()
        )
}