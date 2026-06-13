package com.travelsync.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelsync.core.domain.model.Expense
import com.travelsync.core.domain.model.Trip
import com.travelsync.core.domain.repository.ExpenseRepository
import com.travelsync.core.domain.repository.TripRepository
import com.travelsync.core.domain.usecase.BudgetSummary
import com.travelsync.core.domain.usecase.BudgetSummaryCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    tripRepository: TripRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val tripId: String = checkNotNull(savedStateHandle["tripId"])

    private val tripFlow = tripRepository.getTrip(tripId)
    private val expensesFlow = expenseRepository.getExpenses(tripId)

    val trip: StateFlow<Trip?> = tripFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val expenses = expensesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val summary: StateFlow<BudgetSummary?> = combine(tripFlow, expensesFlow) { trip, expenses ->
        trip?.let { BudgetSummaryCalculator.calculate(it, expenses) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun addExpense(expense: Expense, userId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            expenseRepository.addExpense(
                expense.copy(tripId = tripId, createdByUserId = userId)
            ).onSuccess { onSuccess() }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch { expenseRepository.deleteExpense(expenseId) }
    }
}
