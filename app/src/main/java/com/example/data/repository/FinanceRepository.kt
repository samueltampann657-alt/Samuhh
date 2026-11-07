package com.example.data.repository

import com.example.data.entity.BudgetEntity
import com.example.data.entity.TransactionEntity
import com.example.data.local.TransactionDao
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val dao: TransactionDao) {
    fun getAllTransactions(): Flow<List<TransactionEntity>> = dao.getAllTransactions()

    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<TransactionEntity>> =
        dao.getTransactionsBetween(startTime, endTime)

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        dao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        dao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        dao.deleteTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) =
        dao.deleteTransactionById(id)

    fun getAllBudgets(): Flow<List<BudgetEntity>> = dao.getAllBudgets()

    suspend fun insertBudget(budget: BudgetEntity): Long =
        dao.insertBudget(budget)

    suspend fun updateBudget(budget: BudgetEntity) =
        dao.updateBudget(budget)

    suspend fun deleteBudget(budget: BudgetEntity) =
        dao.deleteBudget(budget)

    suspend fun deleteBudgetById(id: Long) =
        dao.deleteBudgetById(id)
}
