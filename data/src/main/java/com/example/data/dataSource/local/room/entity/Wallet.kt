package com.example.data.dataSource.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.constant.tableColumn.WalletColumn
import com.example.domain.constant.tableColumn.WalletGroupColumn
import java.math.BigDecimal

@Entity(
    tableName = WalletColumn.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = WalletGroup::class,
            parentColumns = [WalletGroupColumn.COLUMN_ID],
            childColumns = [WalletColumn.COLUMN_WALLET_GROUP_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(
        value = [WalletColumn.COLUMN_WALLET_GROUP_ID, WalletColumn.COLUMN_NAME],
        unique = true
    )]
)

data class Wallet(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = WalletColumn.COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = WalletColumn.COLUMN_NAME)
    val name: String,

    @ColumnInfo(name = WalletColumn.COLUMN_BALANCE)
    val balance: BigDecimal = BigDecimal.ZERO,

    @ColumnInfo(name = WalletColumn.COLUMN_WALLET_GROUP_ID)
    val walletGroupId: Int
)