package com.example.data.model

enum class WalletType(val displayName: String, val iconLabel: String) {
    BANK("Rekening Bank", "🏦"),
    CASH("Uang Tunai (Cash)", "💵"),
    E_WALLET("E-Wallet", "📱"),
    SHARED_SAVINGS("Tabungan Keluarga", "💰");

    companion object {
        fun fromDisplayName(name: String): WalletType {
            return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: BANK
        }
    }
}
