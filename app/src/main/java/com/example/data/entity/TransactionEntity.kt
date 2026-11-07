package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Long, // in IDR (Rupiah)
    val type: String, // "EXPENSE" or "INCOME"
    val category: String,
    val memberId: String, // "ayah", "ibu", "anak", "bersama"
    val wallet: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
