package com.example.lankasmartmart.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lankasmartmart.data.local.dao.CartDao
import com.example.lankasmartmart.data.local.dao.SearchHistoryDao
import com.example.lankasmartmart.data.local.dao.TransactionDao
import com.example.lankasmartmart.data.local.entity.CartItemEntity
import com.example.lankasmartmart.data.local.entity.SearchHistoryEntity
import com.example.lankasmartmart.data.local.entity.TransactionEntity
import com.example.lankasmartmart.data.local.entity.TransactionItemEntity

@Database(
    entities = [
        TransactionEntity::class,
        TransactionItemEntity::class,
        CartItemEntity::class,
        SearchHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun cartDao(): CartDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
