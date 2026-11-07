package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.entity.BudgetEntity
import com.example.data.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TransactionEntity::class, BudgetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kas_keluarga_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.transactionDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: TransactionDao) {
                val now = System.currentTimeMillis()
                val oneDay = 86_400_000L

                val sampleTransactions = listOf(
                    TransactionEntity(
                        title = "Gaji Ayah",
                        amount = 12500000L,
                        type = "INCOME",
                        category = "Gaji Pokok",
                        memberId = "ayah",
                        wallet = "Rekening Bank",
                        note = "Gaji bulanan kantor",
                        timestamp = now - (oneDay * 5)
                    ),
                    TransactionEntity(
                        title = "Gaji Ibu",
                        amount = 8000000L,
                        type = "INCOME",
                        category = "Gaji Pokok",
                        memberId = "ibu",
                        wallet = "Rekening Bank",
                        note = "Gaji bulanan",
                        timestamp = now - (oneDay * 5)
                    ),
                    TransactionEntity(
                        title = "Belanja Dapur Mingguan",
                        amount = 1450000L,
                        type = "EXPENSE",
                        category = "Belanja Dapur",
                        memberId = "ibu",
                        wallet = "Rekening Bank",
                        note = "Sayuran, daging, susu & bumbu",
                        timestamp = now - (oneDay * 3)
                    ),
                    TransactionEntity(
                        title = "Listrik PLN & PDAM",
                        amount = 620000L,
                        type = "EXPENSE",
                        category = "Tagihan & Listrik",
                        memberId = "bersama",
                        wallet = "E-Wallet",
                        note = "Token listrik 500rb + PDAM 120rb",
                        timestamp = now - (oneDay * 4)
                    ),
                    TransactionEntity(
                        title = "SPP Sekolah & Les Musik",
                        amount = 950000L,
                        type = "EXPENSE",
                        category = "Pendidikan Anak",
                        memberId = "anak",
                        wallet = "Rekening Bank",
                        note = "Biaya semester dan les piano",
                        timestamp = now - (oneDay * 2)
                    ),
                    TransactionEntity(
                        title = "Bensin & Tol Mobil",
                        amount = 350000L,
                        type = "EXPENSE",
                        category = "Bensin & Transport",
                        memberId = "ayah",
                        wallet = "Uang Tunai (Cash)",
                        note = "Isi Pertamax dan saldo e-toll",
                        timestamp = now - (oneDay * 1)
                    ),
                    TransactionEntity(
                        title = "Makan Siang Keluarga",
                        amount = 280000L,
                        type = "EXPENSE",
                        category = "Makanan & Minuman",
                        memberId = "bersama",
                        wallet = "E-Wallet",
                        note = "Makan bersama akhir pekan",
                        timestamp = now - (oneDay * 1)
                    ),
                    TransactionEntity(
                        title = "Obat & Multivitamin",
                        amount = 185000L,
                        type = "EXPENSE",
                        category = "Kesehatan & Obat",
                        memberId = "bersama",
                        wallet = "Uang Tunai (Cash)",
                        note = "Vitamin C & minyak ikan",
                        timestamp = now
                    )
                )

                val sampleBudgets = listOf(
                    BudgetEntity(category = "Belanja Dapur", monthlyLimit = 3500000L, memberId = "bersama"),
                    BudgetEntity(category = "Tagihan & Listrik", monthlyLimit = 1000000L, memberId = "bersama"),
                    BudgetEntity(category = "Pendidikan Anak", monthlyLimit = 1500000L, memberId = "anak"),
                    BudgetEntity(category = "Bensin & Transport", monthlyLimit = 1200000L, memberId = "ayah"),
                    BudgetEntity(category = "Makanan & Minuman", monthlyLimit = 1500000L, memberId = "bersama"),
                    BudgetEntity(category = "Hiburan & Liburan", monthlyLimit = 1000000L, memberId = "bersama")
                )

                dao.insertTransactions(sampleTransactions)
                dao.insertBudgets(sampleBudgets)
            }
        }
    }
}
