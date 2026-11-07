package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryInfo(
    val name: String,
    val type: TransactionType,
    val icon: ImageVector,
    val color: Color
)

object Categories {
    val expenseCategories = listOf(
        CategoryInfo("Belanja Dapur", TransactionType.EXPENSE, Icons.Default.ShoppingCart, Color(0xFF10B981)),
        CategoryInfo("Makanan & Minuman", TransactionType.EXPENSE, Icons.Default.LocalDining, Color(0xFFF97316)),
        CategoryInfo("Tagihan & Listrik", TransactionType.EXPENSE, Icons.Default.Receipt, Color(0xFFEAB308)),
        CategoryInfo("Pendidikan Anak", TransactionType.EXPENSE, Icons.Default.School, Color(0xFF3B82F6)),
        CategoryInfo("Kesehatan & Obat", TransactionType.EXPENSE, Icons.Default.LocalHospital, Color(0xFFEC4899)),
        CategoryInfo("Bensin & Transport", TransactionType.EXPENSE, Icons.Default.DirectionsCar, Color(0xFF6366F1)),
        CategoryInfo("Kebutuhan Rumah", TransactionType.EXPENSE, Icons.Default.Home, Color(0xFF0D9488)),
        CategoryInfo("Hiburan & Liburan", TransactionType.EXPENSE, Icons.Default.Theaters, Color(0xFF8B5CF6)),
        CategoryInfo("Pengeluaran Lain", TransactionType.EXPENSE, Icons.Default.Category, Color(0xFF64748B))
    )

    val incomeCategories = listOf(
        CategoryInfo("Gaji Pokok", TransactionType.INCOME, Icons.Default.Payments, Color(0xFF059669)),
        CategoryInfo("Bonus & THR", TransactionType.INCOME, Icons.Default.CardGiftcard, Color(0xFFD97706)),
        CategoryInfo("Usaha & Jualan", TransactionType.INCOME, Icons.Default.Store, Color(0xFF2563EB)),
        CategoryInfo("Investasi & Dividen", TransactionType.INCOME, Icons.Default.TrendingUp, Color(0xFF7C3AED)),
        CategoryInfo("Pemasukan Lain", TransactionType.INCOME, Icons.Default.AccountBalance, Color(0xFF0891B2))
    )

    fun getCategoryInfo(name: String, type: TransactionType): CategoryInfo {
        val list = if (type == TransactionType.EXPENSE) expenseCategories else incomeCategories
        return list.find { it.name.equals(name, ignoreCase = true) }
            ?: (if (type == TransactionType.EXPENSE) expenseCategories.last() else incomeCategories.last())
    }
}
