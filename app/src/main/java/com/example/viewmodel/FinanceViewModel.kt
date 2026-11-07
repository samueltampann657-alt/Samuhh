package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entity.BudgetEntity
import com.example.data.entity.TransactionEntity
import com.example.data.local.AppDatabase
import com.example.data.model.FamilyMember
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class CategorySpend(
    val category: String,
    val totalAmount: Long,
    val percentage: Float
)

data class MemberSpend(
    val member: FamilyMember,
    val totalAmount: Long,
    val percentage: Float
)

data class BudgetProgress(
    val budget: BudgetEntity,
    val spent: Long,
    val percentage: Float, // 0.0f to 1.0f or higher if exceeded
    val isOverBudget: Boolean
)

data class MonthlyFinanceSummary(
    val totalBalance: Long,
    val totalIncome: Long,
    val totalExpense: Long,
    val netSavings: Long,
    val monthYearText: String
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = FinanceRepository(database.transactionDao())
    }

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedMemberId = MutableStateFlow<String?>(null) // null = all
    val selectedMemberId: StateFlow<String?> = _selectedMemberId.asStateFlow()

    private val _monthOffset = MutableStateFlow(0) // 0 = this month, -1 = last month
    val monthOffset: StateFlow<Int> = _monthOffset.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val allTransactions = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBudgets = repository.getAllBudgets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Month Boundaries helper
    private fun getMonthBoundaries(offset: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, offset)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startMillis = cal.timeInMillis

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val endMillis = cal.timeInMillis

        return Pair(startMillis, endMillis)
    }

    // Filtered transactions for the selected month, member, and search query
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        _selectedMemberId,
        _monthOffset,
        _searchQuery
    ) { transactions, memberId, offset, query ->
        val (start, end) = getMonthBoundaries(offset)
        transactions.filter { tx ->
            val inMonth = tx.timestamp in start..end
            val matchesMember = memberId == null || tx.memberId.equals(memberId, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    tx.title.contains(query, ignoreCase = true) ||
                    tx.category.contains(query, ignoreCase = true) ||
                    tx.note.contains(query, ignoreCase = true)
            inMonth && matchesMember && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly summary calculation
    val monthlySummary: StateFlow<MonthlyFinanceSummary> = combine(
        allTransactions,
        _selectedMemberId,
        _monthOffset
    ) { transactions, memberId, offset ->
        val (start, end) = getMonthBoundaries(offset)
        val cal = Calendar.getInstance().apply { add(Calendar.MONTH, offset) }
        val monthNames = arrayOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        val monthYearText = "${monthNames[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"

        var totalInc = 0L
        var totalExp = 0L

        // Overall balance for all-time
        var allTimeInc = 0L
        var allTimeExp = 0L
        for (tx in transactions) {
            if (memberId == null || tx.memberId.equals(memberId, ignoreCase = true)) {
                if (tx.type == "INCOME") allTimeInc += tx.amount else allTimeExp += tx.amount
                if (tx.timestamp in start..end) {
                    if (tx.type == "INCOME") totalInc += tx.amount else totalExp += tx.amount
                }
            }
        }

        MonthlyFinanceSummary(
            totalBalance = allTimeInc - allTimeExp,
            totalIncome = totalInc,
            totalExpense = totalExp,
            netSavings = totalInc - totalExp,
            monthYearText = monthYearText
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MonthlyFinanceSummary(0, 0, 0, 0, "Bulan Ini")
    )

    // Category breakdown for current month
    val categorySpends: StateFlow<List<CategorySpend>> = combine(
        filteredTransactions,
        _monthOffset
    ) { transactions, _ ->
        val expenses = transactions.filter { it.type == "EXPENSE" }
        val total = expenses.sumOf { it.amount }
        if (total == 0L) emptyList()
        else {
            expenses.groupBy { it.category }
                .map { (cat, list) ->
                    val sum = list.sumOf { it.amount }
                    CategorySpend(
                        category = cat,
                        totalAmount = sum,
                        percentage = (sum.toFloat() / total.toFloat())
                    )
                }
                .sortedByDescending { it.totalAmount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Spending by member
    val memberSpends: StateFlow<List<MemberSpend>> = combine(
        allTransactions,
        _monthOffset
    ) { transactions, offset ->
        val (start, end) = getMonthBoundaries(offset)
        val monthlyExpenses = transactions.filter { it.timestamp in start..end && it.type == "EXPENSE" }
        val total = monthlyExpenses.sumOf { it.amount }
        if (total == 0L) {
            FamilyMember.entries.map { MemberSpend(it, 0L, 0f) }
        } else {
            FamilyMember.entries.map { member ->
                val sum = monthlyExpenses
                    .filter { it.memberId.equals(member.id, ignoreCase = true) }
                    .sumOf { it.amount }
                MemberSpend(
                    member = member,
                    totalAmount = sum,
                    percentage = (sum.toFloat() / total.toFloat())
                )
            }.sortedByDescending { it.totalAmount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Budget Progress against expenses
    val budgetProgress: StateFlow<List<BudgetProgress>> = combine(
        allBudgets,
        allTransactions,
        _monthOffset
    ) { budgets, transactions, offset ->
        val (start, end) = getMonthBoundaries(offset)
        val monthlyExpenses = transactions.filter { it.timestamp in start..end && it.type == "EXPENSE" }

        budgets.map { b ->
            val spent = monthlyExpenses
                .filter { it.category.equals(b.category, ignoreCase = true) }
                .sumOf { it.amount }
            val ratio = if (b.monthlyLimit > 0) spent.toFloat() / b.monthlyLimit.toFloat() else 0f
            BudgetProgress(
                budget = b,
                spent = spent,
                percentage = ratio,
                isOverBudget = spent > b.monthlyLimit
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setSelectedMember(memberId: String?) {
        _selectedMemberId.value = memberId
    }

    fun previousMonth() {
        _monthOffset.value -= 1
    }

    fun nextMonth() {
        if (_monthOffset.value < 0) {
            _monthOffset.value += 1
        }
    }

    fun resetMonth() {
        _monthOffset.value = 0
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addTransaction(
        title: String,
        amount: Long,
        type: String,
        category: String,
        memberId: String,
        wallet: String,
        note: String,
        timestamp: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    title = title.trim(),
                    amount = amount,
                    type = type,
                    category = category,
                    memberId = memberId,
                    wallet = wallet,
                    note = note.trim(),
                    timestamp = timestamp
                )
            )
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun addOrUpdateBudget(category: String, monthlyLimit: Long, memberId: String = "bersama") {
        viewModelScope.launch {
            val existing = allBudgets.value.find { it.category.equals(category, ignoreCase = true) }
            if (existing != null) {
                repository.updateBudget(existing.copy(monthlyLimit = monthlyLimit))
            } else {
                repository.insertBudget(
                    BudgetEntity(
                        category = category,
                        monthlyLimit = monthlyLimit,
                        memberId = memberId
                    )
                )
            }
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }
}
