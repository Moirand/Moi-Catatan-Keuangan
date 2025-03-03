package com.example.data.dataSource.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.constant.CategoryType
import com.example.domain.constant.tableColumn.CategoryColumn

@Entity(
    tableName = CategoryColumn.TABLE_NAME,
    indices = [
        Index(value = [CategoryColumn.COLUMN_TYPE]),
        Index(
            value = [CategoryColumn.COLUMN_NAME, CategoryColumn.COLUMN_TYPE],
            unique = true
        )
    ]
)

data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = CategoryColumn.COLUMN_ID)
    val id: Int = 0,

    @ColumnInfo(name = CategoryColumn.COLUMN_NAME)
    val name: String,

    @ColumnInfo(name = CategoryColumn.COLUMN_TYPE)
    val type: CategoryType
)