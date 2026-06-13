package com.travelsync.core.data.repository

import com.travelsync.core.data.local.dao.ExpenseDao
import com.travelsync.core.data.local.mapper.expenseEntityFromDomain
import com.travelsync.core.data.local.mapper.toDomain
import com.travelsync.core.domain.model.Expense
import com.travelsync.core.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override fun getExpenses(tripId: String): Flow<List<Expense>> =
        expenseDao.getExpensesForTrip(tripId).map { list -> list.map { it.toDomain() } }

    override suspend fun addExpense(expense: Expense): Result<String> = runCatching {
        val id = expense.id.ifEmpty { UUID.randomUUID().toString() }
        expenseDao.insertExpense(expenseEntityFromDomain(expense.copy(id = id)))
        id
    }

    override suspend fun updateExpense(expense: Expense): Result<Unit> = runCatching {
        val existing = expenseDao.getExpenseOnce(expense.id)
            ?: throw IllegalStateException("Expense not found")
        expenseDao.insertExpense(
            expenseEntityFromDomain(expense).copy(
                createdAt = existing.createdAt,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deleteExpense(expenseId: String): Result<Unit> = runCatching {
        expenseDao.deleteExpense(expenseId)
    }
}
