package com.example.data.dataSource.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.constant.TransactionType
import com.example.domain.constant.tableColumn.CategoryColumn
import com.example.domain.constant.tableColumn.TransactionColumn
import com.example.domain.constant.tableColumn.WalletColumn
import java.math.BigDecimal

@Entity(
    tableName = TransactionColumn.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = [CategoryColumn.COLUMN_ID],
            childColumns = [TransactionColumn.COLUMN_CATEGORY_ID],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Wallet::class,
            parentColumns = [WalletColumn.COLUMN_ID],
            childColumns = [TransactionColumn.COLUMN_FROM_WALLET_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Wallet::class,
            parentColumns = [WalletColumn.COLUMN_ID],
            childColumns = [TransactionColumn.COLUMN_TO_WALLET_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [TransactionColumn.COLUMN_DATE_TIME, TransactionColumn.COLUMN_TYPE, TransactionColumn.COLUMN_CATEGORY_ID, TransactionColumn.COLUMN_FROM_WALLET_ID])]
)

data class Transaction(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = TransactionColumn.COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = TransactionColumn.COLUMN_DATE_TIME)
    val dateTime: String,

    @ColumnInfo(name = TransactionColumn.COLUMN_TYPE)
    val type: TransactionType,

    @ColumnInfo(name = TransactionColumn.COLUMN_CATEGORY_ID)
    val categoryId: Int?,

    @ColumnInfo(name = TransactionColumn.COLUMN_FROM_WALLET_ID)
    val fromWalletId: Int,

    @ColumnInfo(name = TransactionColumn.COLUMN_TO_WALLET_ID)
    val toWalletId: Int?,

    @ColumnInfo(name = TransactionColumn.COLUMN_AMOUNT)
    val amount: BigDecimal,

    @ColumnInfo(name = TransactionColumn.COLUMN_TRANSFER_FEE)
    val transferFee: BigDecimal,

    @ColumnInfo(name = TransactionColumn.COLUMN_NOTE)
    val note: String?,

    @ColumnInfo(name = TransactionColumn.COLUMN_RECEIPT, typeAffinity = ColumnInfo.BLOB)
    val receipt: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transaction

        if (id != other.id) return false
        if (dateTime != other.dateTime) return false
        if (type != other.type) return false
        if (categoryId != other.categoryId) return false
        if (fromWalletId != other.fromWalletId) return false
        if (toWalletId != other.toWalletId) return false
        if (amount != other.amount) return false
        if (transferFee != other.transferFee) return false
        if (note != other.note) return false
        if (receipt != null) {
            if (other.receipt == null) return false
            if (!receipt.contentEquals(other.receipt)) return false
        } else if (other.receipt != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + dateTime.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (categoryId ?: 0)
        result = 31 * result + fromWalletId
        result = 31 * result + (toWalletId ?: 0)
        result = 31 * result + amount.hashCode()
        result = 31 * result + transferFee.hashCode()
        result = 31 * result + (note?.hashCode() ?: 0)
        result = 31 * result + (receipt?.contentHashCode() ?: 0)
        return result
    }
}