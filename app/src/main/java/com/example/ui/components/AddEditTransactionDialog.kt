package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.entity.TransactionEntity
import com.example.data.model.Categories
import com.example.data.model.FamilyMember
import com.example.data.model.TransactionType
import com.example.data.model.WalletType
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.CurrencyFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionDialog(
    transactionToEdit: TransactionEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        amount: Long,
        type: String,
        category: String,
        memberId: String,
        wallet: String,
        note: String
    ) -> Unit
) {
    val isEditing = transactionToEdit != null

    var selectedType by remember {
        mutableStateOf(
            if (transactionToEdit?.type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE
        )
    }

    var title by remember { mutableStateOf(transactionToEdit?.title ?: "") }
    var amountText by remember {
        mutableStateOf(transactionToEdit?.amount?.toString() ?: "")
    }

    val availableCategories = if (selectedType == TransactionType.EXPENSE) {
        Categories.expenseCategories
    } else {
        Categories.incomeCategories
    }

    var selectedCategory by remember(selectedType) {
        val initial = transactionToEdit?.category
        mutableStateOf(
            if (initial != null && availableCategories.any { it.name.equals(initial, ignoreCase = true) }) {
                initial
            } else {
                availableCategories.first().name
            }
        )
    }

    var selectedMemberId by remember {
        mutableStateOf(transactionToEdit?.memberId ?: "bersama")
    }

    var selectedWallet by remember {
        mutableStateOf(transactionToEdit?.wallet ?: WalletType.BANK.displayName)
    }

    var note by remember { mutableStateOf(transactionToEdit?.note ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "Ubah Transaksi" else "Tambah Transaksi Baru",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dialog_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Type Switcher: Pemasukan vs Pengeluaran
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Pengeluaran
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedType == TransactionType.EXPENSE) ExpenseRed else Color.Transparent)
                            .clickable { selectedType = TransactionType.EXPENSE }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💸 Pengeluaran",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == TransactionType.EXPENSE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Pemasukan
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedType == TransactionType.INCOME) IncomeGreen else Color.Transparent)
                            .clickable { selectedType = TransactionType.INCOME }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💰 Pemasukan",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == TransactionType.INCOME) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Amount Input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        amountText = digits
                        errorMessage = null
                    },
                    label = { Text("Nominal (Rupiah)") },
                    placeholder = { Text("cth. 150000") },
                    leadingIcon = {
                        Text(
                            "Rp",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_transaction_amount"),
                    shape = RoundedCornerShape(14.dp)
                )

                // Quick Amount Adders
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickAdditions = listOf(
                        "+50rb" to 50_000L,
                        "+100rb" to 100_000L,
                        "+250rb" to 250_000L,
                        "+500rb" to 500_000L,
                        "+1jt" to 1_000_000L
                    )
                    quickAdditions.forEach { (label, value) ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                val current = amountText.toLongOrNull() ?: 0L
                                amountText = (current + value).toString()
                            },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = { Text("Keterangan / Judul") },
                    placeholder = { Text("cth. Belanja Sayur & Buah") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_transaction_title"),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Family Member Selector
                Text(
                    text = "Anggota Keluarga",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FamilyMember.entries.forEach { member ->
                        val isSelected = selectedMemberId.equals(member.id, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMemberId = member.id },
                            label = { Text("${member.emoji} ${member.displayName}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = member.color,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selector
                Text(
                    text = "Kategori",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableCategories.forEach { cat ->
                        val isSelected = selectedCategory.equals(cat.name, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat.name },
                            leadingIcon = {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else cat.color,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(cat.name, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = cat.color,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wallet / Account Source
                Text(
                    text = "Dompet / Sumber Dana",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WalletType.entries.forEach { wallet ->
                        val isSelected = selectedWallet == wallet.displayName
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedWallet = wallet.displayName },
                            label = { Text("${wallet.iconLabel} ${wallet.displayName}") },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Note (Optional)
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Catatan Tambahan (Opsional)") },
                    placeholder = { Text("Tambahkan rincian bila perlu") },
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            val amountVal = amountText.toLongOrNull()
                            if (amountVal == null || amountVal <= 0L) {
                                errorMessage = "Masukkan nominal transaksi yang valid"
                                return@Button
                            }
                            if (title.isBlank()) {
                                errorMessage = "Keterangan transaksi tidak boleh kosong"
                                return@Button
                            }

                            onSave(
                                title,
                                amountVal,
                                selectedType.name,
                                selectedCategory,
                                selectedMemberId,
                                selectedWallet,
                                note
                            )
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("save_transaction_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == TransactionType.EXPENSE) ExpenseRed else IncomeGreen
                        )
                    ) {
                        Text(
                            text = if (isEditing) "Simpan Perubahan" else "Simpan Transaksi",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
