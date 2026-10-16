package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EqualColor
import com.example.ui.theme.EqualColorPressed
import com.example.ui.theme.FunctionColorDark
import com.example.ui.theme.FunctionColorLight

enum class ButtonType {
    NUMBER,
    OPERATOR,
    FUNCTION,
    EQUAL,
    SECONDARY
}

@Composable
fun CalculatorButton(
    text: String,
    type: ButtonType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    fontSize: TextUnit = 24.sp
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "btn_scale"
    )

    val isDark = MaterialTheme.colorScheme.background.value == com.example.ui.theme.DarkBg.value

    val targetContainerColor = when (type) {
        ButtonType.NUMBER -> {
            if (isDark) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        }
        ButtonType.OPERATOR -> {
            if (isDark) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primaryContainer
        }
        ButtonType.FUNCTION -> {
            if (isDark) MaterialTheme.colorScheme.error.copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
        }
        ButtonType.EQUAL -> {
            if (isPressed) EqualColorPressed else EqualColor
        }
        ButtonType.SECONDARY -> {
            if (isDark) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        }
    }

    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        label = "btn_color"
    )

    val contentColor = when (type) {
        ButtonType.NUMBER -> MaterialTheme.colorScheme.onSurface
        ButtonType.OPERATOR -> if (isDark) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary
        ButtonType.FUNCTION -> if (isDark) FunctionColorDark else FunctionColorLight
        ButtonType.EQUAL -> Color.White
        ButtonType.SECONDARY -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier
            .padding(4.dp)
            .scale(scale)
            .testTag("calc_btn_$text"),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource,
        shadowElevation = if (type == ButtonType.EQUAL) 4.dp else 1.dp,
        tonalElevation = if (type == ButtonType.EQUAL) 6.dp else 2.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                icon()
            } else {
                Text(
                    text = text,
                    fontSize = fontSize,
                    fontWeight = when (type) {
                        ButtonType.NUMBER -> FontWeight.Normal
                        ButtonType.EQUAL, ButtonType.OPERATOR -> FontWeight.Bold
                        else -> FontWeight.Medium
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
