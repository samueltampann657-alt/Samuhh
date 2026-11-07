package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.BudgetEntity
import com.example.data.entity.TransactionEntity
import com.example.ui.components.AddBudgetDialog
import com.example.ui.components.AddEditTransactionDialog
import com.example.ui.components.BudgetSection
import com.example.ui.components.FamilyMemberChips
import com.example.ui.components.StatisticsSection
import com.example.ui.components.SummaryBalanceCard
import com.example.ui.components.TransactionItemCard
import com.example.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedMemberId by viewModel.selectedMemberId.collectAsState()
    val summary by viewModel.monthlySummary.collectAsState()
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()
    val categorySpends by viewModel.categorySpends.collectAsState()
    val memberSpends by viewModel.memberSpends.collectAsState()
    val budgetProgressList by viewModel.budgetProgress.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<TransactionEntity?>(null) }
    var transactionToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var budgetToEdit by remember { mutableStateOf<BudgetEntity?>(null) }
    var budgetToDelete by remember { mutableStateOf<BudgetEntity?>(null) }

    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        if (isSearchActive) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = { Text("Cari transaksi...") },
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = {
                                        viewModel.setSearchQuery("")
                                        isSearchActive = false
                                    }) {
                                        Icon(Icons.Default.Close, contentDescription = "Tutup Pencarian")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("top_search_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "KasKeluarga",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.5).sp
                                    )
                                )
                            }
                        }
                    },
                    actions = {
                        if (!isSearchActive) {
                            // Month Selector
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.previousMonth() },
                                    modifier = Modifier.testTag("prev_month_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Bulan Sebelumnya",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Text(
                                    text = summary.monthYearText,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                IconButton(
                                    onClick = { viewModel.nextMonth() },
                                    modifier = Modifier.testTag("next_month_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Bulan Berikutnya",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { isSearchActive = true },
                                    modifier = Modifier.testTag("open_search_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Cari"
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Family Member Filter Chips
                FamilyMemberChips(
                    selectedMemberId = selectedMemberId,
                    onSelectMember = { viewModel.setSelectedMember(it) }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Ringkasan") },
                    modifier = Modifier.testTag("tab_overview")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                    label = { Text("Transaksi") },
                    modifier = Modifier.testTag("tab_transactions")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    icon = { Icon(Icons.Default.TrackChanges, contentDescription = null) },
                    label = { Text("Anggaran") },
                    modifier = Modifier.testTag("tab_budget")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.setSelectedTab(3) },
                    icon = { Icon(Icons.Default.PieChart, contentDescription = null) },
                    label = { Text("Statistik") },
                    modifier = Modifier.testTag("tab_stats")
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddTransactionDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Catat Transaksi", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_transaction")
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 720.dp)
            ) {
                when (selectedTab) {
                    0 -> OverviewTabContent(
                        summary = summary,
                        recentTransactions = filteredTransactions.take(5),
                        budgetProgressList = budgetProgressList.take(3),
                        onSeeAllTransactions = { viewModel.setSelectedTab(1) },
                        onSeeAllBudgets = { viewModel.setSelectedTab(2) },
                        onEditTransaction = { transactionToEdit = it },
                        onDeleteTransaction = { transactionToDelete = it }
                    )
                    1 -> TransactionsTabContent(
                        transactions = filteredTransactions,
                        onEditTransaction = { transactionToEdit = it },
                        onDeleteTransaction = { transactionToDelete = it }
                    )
                    2 -> BudgetSection(
                        budgetProgressList = budgetProgressList,
                        onAddBudgetClick = {
                            budgetToEdit = null
                            showAddBudgetDialog = true
                        },
                        onEditBudgetClick = {
                            budgetToEdit = it
                            showAddBudgetDialog = true
                        },
                        onDeleteBudgetClick = { budgetToDelete = it }
                    )
                    3 -> StatisticsSection(
                        summary = summary,
                        memberSpends = memberSpends,
                        categorySpends = categorySpends
                    )
                }
            }
        }
    }

    // Add / Edit Transaction Dialog
    if (showAddTransactionDialog || transactionToEdit != null) {
        AddEditTransactionDialog(
            transactionToEdit = transactionToEdit,
            onDismiss = {
                showAddTransactionDialog = false
                transactionToEdit = null
            },
            onSave = { title, amount, type, category, memberId, wallet, note ->
                if (transactionToEdit != null) {
                    viewModel.updateTransaction(
                        transactionToEdit!!.copy(
                            title = title,
                            amount = amount,
                            type = type,
                            category = category,
                            memberId = memberId,
                            wallet = wallet,
                            note = note
                        )
                    )
                } else {
                    viewModel.addTransaction(
                        title = title,
                        amount = amount,
                        type = type,
                        category = category,
                        memberId = memberId,
                        wallet = wallet,
                        note = note
                    )
                }
                showAddTransactionDialog = false
                transactionToEdit = null
            }
        )
    }

    // Add / Edit Budget Dialog
    if (showAddBudgetDialog) {
        AddBudgetDialog(
            initialBudget = budgetToEdit,
            onDismiss = {
                showAddBudgetDialog = false
                budgetToEdit = null
            },
            onSave = { category, limit ->
                viewModel.addOrUpdateBudget(category, limit)
                showAddBudgetDialog = false
                budgetToEdit = null
            }
        )
    }

    // Delete Transaction Confirmation
    transactionToDelete?.let { tx ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Hapus Transaksi?") },
            text = { Text("Apakah Anda yakin ingin menghapus catatan \"${tx.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(tx)
                        transactionToDelete = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Delete Budget Confirmation
    budgetToDelete?.let { b ->
        AlertDialog(
            onDismissRequest = { budgetToDelete = null },
            title = { Text("Hapus Anggaran?") },
            text = { Text("Apakah Anda yakin ingin menghapus target anggaran untuk kategori \"${b.category}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBudget(b)
                        budgetToDelete = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { budgetToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun OverviewTabContent(
    summary: com.example.viewmodel.MonthlyFinanceSummary,
    recentTransactions: List<TransactionEntity>,
    budgetProgressList: List<com.example.viewmodel.BudgetProgress>,
    onSeeAllTransactions: () -> Unit,
    onSeeAllBudgets: () -> Unit,
    onEditTransaction: (TransactionEntity) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("overview_tab_content"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Card
        item {
            SummaryBalanceCard(summary = summary)
        }

        // Budget Snapshot Section
        if (budgetProgressList.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎯 Target Anggaran",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onSeeAllBudgets) {
                        Text("Lihat Semua")
                    }
                }
            }

            items(budgetProgressList, key = { "snap_${it.budget.id}" }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.budget.category, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${(item.percentage * 100).toInt()}%",
                                fontWeight = FontWeight.Bold,
                                color = if (item.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { item.percentage.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = if (item.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 Transaksi Terbaru",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onSeeAllTransactions) {
                    Text("Lihat Semua")
                }
            }
        }

        if (recentTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Belum Ada Catatan Transaksi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ketuk tombol '+ Catat Transaksi' untuk memulai pencatatan kas keluarga.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(recentTransactions, key = { it.id }) { tx ->
                TransactionItemCard(
                    transaction = tx,
                    onEdit = onEditTransaction,
                    onDelete = onDeleteTransaction
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun TransactionsTabContent(
    transactions: List<TransactionEntity>,
    onEditTransaction: (TransactionEntity) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (transactions.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔍", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tidak Ada Transaksi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tidak ditemukan transaksi untuk anggota keluarga atau filter periode yang dipilih.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("transactions_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(transactions, key = { it.id }) { tx ->
                TransactionItemCard(
                    transaction = tx,
                    onEdit = onEditTransaction,
                    onDelete = onDeleteTransaction
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
