package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.BudgetEntity
import com.example.data.model.Categories

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddBudgetDialog(
    initialBudget: BudgetEntity? = null,
    onDismiss: () -> Unit,
    onSave: (category: String, limit: Long) -> Unit
) {
    var selectedCategory by remember {
        mutableStateOf(initialBudget?.category ?: Categories.expenseCategories.first().name)
    }
    var limitText by remember {
        mutableStateOf(initialBudget?.monthlyLimit?.toString() ?: "")
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialBudget != null) "Ubah Target Anggaran" else "Pasang Anggaran Kategori",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Pilih Kategori",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Categories.expenseCategories.forEach { cat ->
                        val isSelected = selectedCategory == cat.name
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat.name },
                            label = { Text(cat.name, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = cat.color,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = limitText,
                    onValueChange = {
                        limitText = it.filter { ch -> ch.isDigit() }
                        errorMessage = null
                    },
                    label = { Text("Batas Anggaran Bulanan") },
                    placeholder = { Text("cth. 2500000") },
                    leadingIcon = {
                        Text("Rp", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_budget_limit"),
                    shape = RoundedCornerShape(14.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            val limitVal = limitText.toLongOrNull()
                            if (limitVal == null || limitVal <= 0L) {
                                errorMessage = "Masukkan batas anggaran yang valid"
                                return@Button
                            }
                            onSave(selectedCategory, limitVal)
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("save_budget_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Simpan Anggaran", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
