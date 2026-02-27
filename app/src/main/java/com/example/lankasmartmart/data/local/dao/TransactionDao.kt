package com.example.lankasmartmart.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.lankasmartmart.data.local.entity.TransactionEntity
import com.example.lankasmartmart.data.local.entity.TransactionItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionItems(items: List<TransactionItemEntity>)

    @Transaction
    suspend fun insertTransactionWithItems(
        transaction: TransactionEntity,
        items: List<TransactionItemEntity>
    ) {
        insertTransaction(transaction)
        insertTransactionItems(items)
    }

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTransactionsByUser(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: String): TransactionEntity?

    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    suspend fun getTransactionItems(transactionId: String): List<TransactionItemEntity>

    @Query("UPDATE transactions SET status = :status, updatedAt = :updatedAt WHERE id = :transactionId")
    suspend fun updateTransactionStatus(transactionId: String, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM transactions WHERE createdAt < :cutoffTime")
    suspend fun deleteOldTransactions(cutoffTime: Long)

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()
}
