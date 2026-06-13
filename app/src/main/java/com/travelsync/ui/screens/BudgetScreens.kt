package com.travelsync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.travelsync.core.domain.model.ExpenseCategory
import com.travelsync.core.domain.model.Trip
import com.travelsync.core.domain.usecase.BudgetSummary
import com.travelsync.ui.components.GradientBackground
import com.travelsync.ui.components.TravelSyncTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BudgetDashboardScreen(
    trip: Trip,
    summary: BudgetSummary,
    expenses: List<com.travelsync.core.domain.model.Expense>,
    onAddExpenseClick: () -> Unit,
    onDeleteExpense: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    GradientBackground {
        Scaffold(
            topBar = {
                TravelSyncTopBar(
                    title = "${trip.title} Budget",
                    onBackClick = onBackClick
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddExpenseClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Expense")
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Total Spent (${trip.currency})", style = MaterialTheme.typography.bodySmall)
                            Text(
                                String.format(Locale.US, "%.2f", summary.totalSpent),
                                style = MaterialTheme.typography.headlineMedium
                            )
                            summary.remaining?.let { remaining ->
                                Text(
                                    text = "Remaining: ${String.format(Locale.US, "%.2f", remaining)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                if (summary.byCategory.isNotEmpty()) {
                    item { Text("By Category", style = MaterialTheme.typography.titleLarge) }
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                summary.byCategory.forEach { (category, amount) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(category.name.lowercase().replaceFirstChar { it.uppercase() })
                                        Text(String.format(Locale.US, "%.2f", amount))
                                    }
                                }
                            }
                        }
                    }
                }

                item { Text("Expense Log", style = MaterialTheme.typography.titleLarge) }

                if (expenses.isEmpty()) {
                    item { Text("No expenses yet.") }
                } else {
                    items(expenses, key = { it.id }) { expense ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Payments, contentDescription = null)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = expense.title, style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        text = sdf.format(expense.expenseDate),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                                Text(
                                    text = String.format(Locale.US, "%.2f", expense.amount),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                IconButton(onClick = { onDeleteExpense(expense.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete expense")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddExpenseScreen(
    currency: String,
    onExpenseSaved: (title: String, amount: Double, category: ExpenseCategory) -> Unit,
    onBackClick: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ExpenseCategory.FOOD) }

    GradientBackground {
        Scaffold(
            topBar = { TravelSyncTopBar(title = "Add Expense", onBackClick = onBackClick) },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount ($currency)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Text("Category", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpenseCategory.entries.take(4).forEach { item ->
                        FilterChip(
                            selected = category == item,
                            onClick = { category = item },
                            label = { Text(item.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        val parsed = amount.toDoubleOrNull() ?: return@Button
                        if (title.isNotBlank() && parsed > 0) {
                            onExpenseSaved(title.trim(), parsed, category)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Expense")
                }
            }
        }
    }
}
