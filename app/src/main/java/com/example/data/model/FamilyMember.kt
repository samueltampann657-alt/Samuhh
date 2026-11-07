package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.MemberChild
import com.example.ui.theme.MemberFather
import com.example.ui.theme.MemberMother
import com.example.ui.theme.MemberShared
import com.example.ui.theme.TealDark

enum class FamilyMember(
    val id: String,
    val displayName: String,
    val emoji: String,
    val color: Color,
    val roleDescription: String
) {
    FATHER("ayah", "Ayah", "👨", MemberFather, "Kepala Keluarga"),
    MOTHER("ibu", "Ibu", "👩", MemberMother, "Pengelola Keuangan"),
    CHILD("anak", "Anak", "👧", MemberChild, "Pendidikan & Anak"),
    SHARED("bersama", "Bersama", "🏠", MemberShared, "Kebutuhan Rumah Tangga");

    companion object {
        fun fromId(id: String): FamilyMember {
            return entries.find { it.id.equals(id, ignoreCase = true) || it.displayName.equals(id, ignoreCase = true) }
                ?: SHARED
        }
    }
}
