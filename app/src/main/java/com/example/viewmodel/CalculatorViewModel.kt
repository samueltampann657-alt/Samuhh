package com.example.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.CalculationItem
import com.example.util.CalculatorEvaluator
import com.example.util.EvalResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val previewResult: String? = null,
    val isResultCalculated: Boolean = false,
    val errorMessage: String? = null,
    val history: List<CalculationItem> = emptyList(),
    val isHistoryOpen: Boolean = false,
    val isScientificExpanded: Boolean = false,
    val toastMessage: String? = null
)

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val operators = setOf('+', '−', '×', '÷', '^')

    fun onDigit(digit: String) {
        _uiState.update { state ->
            val newExpr = if (state.isResultCalculated) {
                // If a calculation was just completed, typing a new digit starts a fresh expression
                digit
            } else {
                // Avoid redundant leading zeroes like "0" + "5" -> "5"
                if (state.expression == "0" && digit != "0") {
                    digit
                } else if (state.expression == "0" && digit == "0") {
                    "0"
                } else {
                    state.expression + digit
                }
            }
            state.copy(
                expression = newExpr,
                isResultCalculated = false,
                errorMessage = null
            )
        }
        updatePreview()
    }

    fun onOperator(op: String) {
        val opChar = op.firstOrNull() ?: return
        _uiState.update { state ->
            val current = state.expression
            val newExpr = when {
                current.isEmpty() -> {
                    if (op == "−") "−" else ""
                }
                current.last() in operators -> {
                    // Replace the trailing operator with the new one
                    current.dropLast(1) + op
                }
                current.endsWith(".") -> {
                    current.dropLast(1) + op
                }
                else -> {
                    current + op
                }
            }
            state.copy(
                expression = newExpr,
                isResultCalculated = false,
                errorMessage = null
            )
        }
        updatePreview()
    }

    fun onDecimal() {
        _uiState.update { state ->
            val current = if (state.isResultCalculated) "0" else state.expression
            val lastNumberToken = current.split('+', '−', '×', '÷', '^', '(', ')').lastOrNull() ?: ""

            val newExpr = if (!lastNumberToken.contains('.')) {
                if (current.isEmpty() || current.last() in operators || current.endsWith("(")) {
                    "$current 0."
                } else {
                    "$current."
                }
            } else {
                current
            }
            state.copy(
                expression = newExpr,
                isResultCalculated = false,
                errorMessage = null
            )
        }
        updatePreview()
    }

    fun onParenthesis() {
        _uiState.update { state ->
            val current = if (state.isResultCalculated) "" else state.expression
            val openCount = current.count { it == '(' }
            val closeCount = current.count { it == ')' }

            val newExpr = if (current.isEmpty()) {
                "("
            } else {
                val lastChar = current.last()
                if (lastChar.isDigit() || lastChar == ')' || lastChar == '%') {
                    if (openCount > closeCount) {
                        "$current)"
                    } else {
                        "$current×("
                    }
                } else if (lastChar in operators || lastChar == '(') {
                    "$current("
                } else {
                    "$current("
                }
            }
            state.copy(
                expression = newExpr,
                isResultCalculated = false,
                errorMessage = null
            )
        }
        updatePreview()
    }

    fun onPercentage() {
        _uiState.update { state ->
            val current = state.expression
            if (current.isNotEmpty() && (current.last().isDigit() || current.last() == ')')) {
                state.copy(
                    expression = "$current%",
                    isResultCalculated = false,
                    errorMessage = null
                )
            } else {
                state
            }
        }
        updatePreview()
    }

    fun onSquareRoot() {
        _uiState.update { state ->
            val current = if (state.isResultCalculated) "" else state.expression
            val newExpr = if (current.isNotEmpty() && (current.last().isDigit() || current.last() == ')')) {
                "$current×√("
            } else {
                "$current√("
            }
            state.copy(
                expression = newExpr,
                isResultCalculated = false,
                errorMessage = null
            )
        }
        updatePreview()
    }

    fun onConstant(constant: String) {
        _uiState.update { state ->
            val current = if (state.isResultCalculated) "" else state.expression
            val newExpr = if (current.isNotEmpty() && (current.last().isDigit() || current.last() == ')')) {
                "$current×$constant"
            } else {
                "$current$constant"
            }
            state.copy(
                expression = newExpr,
                isResultCalculated = false,
                errorMessage = null
            )
        }
        updatePreview()
    }

    fun onToggleSign() {
        _uiState.update { state ->
            val current = state.expression
            if (current.isEmpty()) return@update state

            // Check if current is wrapped in (-...)
            if (current.startsWith("(-") && current.endsWith(")")) {
                state.copy(
                    expression = current.substring(2, current.length - 1),
                    isResultCalculated = false
                )
            } else if (current.startsWith("−")) {
                state.copy(
                    expression = current.substring(1),
                    isResultCalculated = false
                )
            } else {
                state.copy(
                    expression = "(-$current)",
                    isResultCalculated = false
                )
            }
        }
        updatePreview()
    }

    fun onBackspace() {
        _uiState.update { state ->
            if (state.isResultCalculated) {
                // If just calculated, backspace clears
                state.copy(expression = "", previewResult = null, isResultCalculated = false)
            } else if (state.expression.isNotEmpty()) {
                val current = state.expression
                val newExpr = if (current.endsWith("√(")) {
                    current.dropLast(2)
                } else {
                    current.dropLast(1)
                }
                state.copy(expression = newExpr, errorMessage = null)
            } else {
                state
            }
        }
        updatePreview()
    }

    fun onClear() {
        _uiState.update { state ->
            state.copy(
                expression = "",
                previewResult = null,
                isResultCalculated = false,
                errorMessage = null
            )
        }
    }

    fun onCalculate() {
        val current = _uiState.value.expression
        if (current.isBlank()) return

        when (val result = CalculatorEvaluator.evaluate(current)) {
            is EvalResult.Success -> {
                val newHistoryItem = CalculationItem(
                    expression = current,
                    result = result.formatted
                )
                _uiState.update { state ->
                    state.copy(
                        expression = result.formatted,
                        previewResult = null,
                        isResultCalculated = true,
                        errorMessage = null,
                        history = listOf(newHistoryItem) + state.history.take(49)
                    )
                }
            }
            is EvalResult.Error -> {
                _uiState.update { state ->
                    state.copy(
                        errorMessage = result.message,
                        previewResult = null
                    )
                }
            }
        }
    }

    fun onSelectHistoryItem(item: CalculationItem, useAsExpression: Boolean = false) {
        _uiState.update { state ->
            state.copy(
                expression = if (useAsExpression) item.expression else item.result,
                previewResult = null,
                isResultCalculated = !useAsExpression,
                isHistoryOpen = false,
                errorMessage = null
            )
        }
        updatePreview()
    }

    fun onClearHistory() {
        _uiState.update { state ->
            state.copy(
                history = emptyList(),
                toastMessage = "Riwayat perhitungan dibersihkan"
            )
        }
    }

    fun onToggleHistory(open: Boolean? = null) {
        _uiState.update { state ->
            state.copy(isHistoryOpen = open ?: !state.isHistoryOpen)
        }
    }

    fun onToggleScientific() {
        _uiState.update { state ->
            state.copy(isScientificExpanded = !state.isScientificExpanded)
        }
    }

    fun onCopyResult(context: Context) {
        val state = _uiState.value
        val textToCopy = if (state.expression.isNotEmpty()) state.expression else state.previewResult
        if (!textToCopy.isNullOrBlank()) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Kalkulator", textToCopy)
            clipboard.setPrimaryClip(clip)
            _uiState.update { it.copy(toastMessage = "Teks disalin: $textToCopy") }
        }
    }

    fun onClearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    private fun updatePreview() {
        viewModelScope.launch {
            val current = _uiState.value.expression
            if (current.isBlank() || _uiState.value.isResultCalculated) {
                _uiState.update { it.copy(previewResult = null) }
                return@launch
            }

            // Only preview if it contains at least one operator or function
            val hasOperator = current.any { it in operators || it == '%' || it == '√' }
            if (!hasOperator) {
                _uiState.update { it.copy(previewResult = null) }
                return@launch
            }

            when (val eval = CalculatorEvaluator.evaluate(current)) {
                is EvalResult.Success -> {
                    // Do not show preview if it is identical to expression
                    if (eval.formatted != current) {
                        _uiState.update { it.copy(previewResult = eval.formatted) }
                    } else {
                        _uiState.update { it.copy(previewResult = null) }
                    }
                }
                is EvalResult.Error -> {
                    _uiState.update { it.copy(previewResult = null) }
                }
            }
        }
    }
}
