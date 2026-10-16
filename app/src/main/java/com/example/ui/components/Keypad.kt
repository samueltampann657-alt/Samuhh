package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Keypad(
    isScientificExpanded: Boolean,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onParenthesis: () -> Unit,
    onPercentage: () -> Unit,
    onToggleSign: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onCalculate: () -> Unit,
    onSquareRoot: () -> Unit,
    onPower: () -> Unit,
    onConstant: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Expandable Scientific Bar
        AnimatedVisibility(
            visible = isScientificExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CalculatorButton(
                    text = "√",
                    type = ButtonType.SECONDARY,
                    onClick = onSquareRoot,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f).height(48.dp)
                )
                CalculatorButton(
                    text = "^",
                    type = ButtonType.SECONDARY,
                    onClick = onPower,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f).height(48.dp)
                )
                CalculatorButton(
                    text = "π",
                    type = ButtonType.SECONDARY,
                    onClick = { onConstant("π") },
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f).height(48.dp)
                )
                CalculatorButton(
                    text = "e",
                    type = ButtonType.SECONDARY,
                    onClick = { onConstant("e") },
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f).height(48.dp)
                )
            }
        }

        // Row 1: AC, ⌫ (Backspace), %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                text = "AC",
                type = ButtonType.FUNCTION,
                onClick = onClear,
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "⌫",
                type = ButtonType.FUNCTION,
                onClick = onBackspace,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Backspace,
                        contentDescription = "Hapus",
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "%",
                type = ButtonType.FUNCTION,
                onClick = onPercentage,
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "÷",
                type = ButtonType.OPERATOR,
                onClick = { onOperator("÷") },
                fontSize = 28.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                text = "7",
                type = ButtonType.NUMBER,
                onClick = { onDigit("7") },
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "8",
                type = ButtonType.NUMBER,
                onClick = { onDigit("8") },
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "9",
                type = ButtonType.NUMBER,
                onClick = { onDigit("9") },
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "×",
                type = ButtonType.OPERATOR,
                onClick = { onOperator("×") },
                fontSize = 28.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                text = "4",
                type = ButtonType.NUMBER,
                onClick = { onDigit("4") },
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "5",
                type = ButtonType.NUMBER,
                onClick = { onDigit("5") },
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "6",
                type = ButtonType.NUMBER,
                onClick = { onDigit("6") },
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "−",
                type = ButtonType.OPERATOR,
                onClick = { onOperator("−") },
                fontSize = 28.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                text = "1",
                type = ButtonType.NUMBER,
                onClick = { onDigit("1") },
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "2",
                type = ButtonType.NUMBER,
                onClick = { onDigit("2") },
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "3",
                type = ButtonType.NUMBER,
                onClick = { onDigit("3") },
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "+",
                type = ButtonType.OPERATOR,
                onClick = { onOperator("+") },
                fontSize = 28.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
        }

        // Row 5: ±, 0, ., = (or ( ), 0, ., =)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                text = "( )",
                type = ButtonType.FUNCTION,
                onClick = onParenthesis,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "0",
                type = ButtonType.NUMBER,
                onClick = { onDigit("0") },
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = ".",
                type = ButtonType.NUMBER,
                onClick = onDecimal,
                fontSize = 28.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
            CalculatorButton(
                text = "=",
                type = ButtonType.EQUAL,
                onClick = onCalculate,
                fontSize = 32.sp,
                modifier = Modifier.weight(1f).aspectRatio(1.25f)
            )
        }
    }
}
