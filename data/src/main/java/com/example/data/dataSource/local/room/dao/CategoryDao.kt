package com.example.data.dataSource.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.dataSource.local.room.entity.Category
import com.example.domain.constant.CategoryType
import com.example.domain.constant.tableColumn.CategoryColumn

@Dao
interface CategoryDao {
    @Query("SELECT * FROM ${CategoryColumn.TABLE_NAME} WHERE ${CategoryColumn.COLUMN_TYPE} = :type")
    fun getCategoriesByType(type: CategoryType): List<Category?>

    @Query("SELECT * FROM ${CategoryColumn.TABLE_NAME} WHERE ${CategoryColumn.COLUMN_ID} = :categoryId")
    fun getCategoryById(categoryId: Int): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategories(categoryList: List<Category>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: Category)

    @Delete
    fun deleteCategory(category: Category)
}