package com.example

import com.example.data.model.Categories
import com.example.data.model.FamilyMember
import com.example.data.model.TransactionType
import com.example.util.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `currency formatting rupiah test`() {
        val formatted = CurrencyFormatter.formatRupiah(1500000L)
        assertTrue(formatted.contains("1.500.000") || formatted.contains("1,500,000"))

        val compact = CurrencyFormatter.formatCompactRupiah(2500000L)
        assertEquals("Rp 2.5 jt", compact)
    }

    @Test
    fun `family member parsing test`() {
        assertEquals(FamilyMember.FATHER, FamilyMember.fromId("ayah"))
        assertEquals(FamilyMember.MOTHER, FamilyMember.fromId("ibu"))
        assertEquals(FamilyMember.CHILD, FamilyMember.fromId("anak"))
        assertEquals(FamilyMember.SHARED, FamilyMember.fromId("bersama"))
    }

    @Test
    fun `category metadata resolution test`() {
        val catInfo = Categories.getCategoryInfo("Belanja Dapur", TransactionType.EXPENSE)
        assertEquals("Belanja Dapur", catInfo.name)
        assertNotNull(catInfo.icon)
    }
}
