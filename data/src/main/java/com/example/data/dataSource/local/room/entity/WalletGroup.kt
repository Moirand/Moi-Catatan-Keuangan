package com.example.data.dataSource.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.constant.tableColumn.WalletGroupColumn

@Entity(
    tableName = WalletGroupColumn.TABLE_NAME,
    indices = [Index(value = [WalletGroupColumn.COLUMN_NAME], unique = true)]
)
data class WalletGroup(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = WalletGroupColumn.COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = WalletGroupColumn.COLUMN_NAME)
    val name: String
)