package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.FamilyMember

@Composable
fun FamilyMemberChips(
    selectedMemberId: String?,
    onSelectMember: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "Semua" Chip
        FilterChip(
            selected = selectedMemberId == null,
            onClick = { onSelectMember(null) },
            label = { Text("👥 Semua Keluarga") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.testTag("filter_member_all")
        )

        // Member Chips
        FamilyMember.entries.forEach { member ->
            val isSelected = selectedMemberId.equals(member.id, ignoreCase = true)
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) onSelectMember(null) else onSelectMember(member.id)
                },
                label = { Text("${member.emoji} ${member.displayName}") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = member.color,
                    selectedLabelColor = androidx.compose.ui.graphics.Color.White
                ),
                modifier = Modifier.testTag("filter_member_${member.id}")
            )
        }
    }
}
