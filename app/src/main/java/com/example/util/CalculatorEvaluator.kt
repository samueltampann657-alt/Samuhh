package com.example.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

sealed class EvalResult {
    data class Success(val value: Double, val formatted: String) : EvalResult()
    data class Error(val message: String) : EvalResult()
}

object CalculatorEvaluator {

    private const val ERROR_DIV_ZERO = "Tidak dapat membagi dengan 0"
    private const val ERROR_SYNTAX = "Format tidak valid"
    private const val ERROR_DOMAIN = "Hasil di luar jangkauan"

    /**
     * Evaluates a mathematical expression string.
     * Supports +, −, ×, ÷, %, ^, √, π, e, and parentheses.
     */
    fun evaluate(rawExpr: String): EvalResult {
        if (rawExpr.isBlank()) {
            return EvalResult.Error(ERROR_SYNTAX)
        }

        // Normalize symbols:
        // '×' -> '*', '÷' -> '/', '−' -> '-', '√' -> 'sqrt'
        var normalized = rawExpr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "${Math.PI}")
            .replace("e", "${Math.E}")
            .replace(" ", "")

        // Handle auto-closing unclosed parentheses for live preview/calculation
        val openCount = normalized.count { it == '(' }
        val closeCount = normalized.count { it == ')' }
        if (openCount > closeCount) {
            normalized += ")".repeat(openCount - closeCount)
        }

        return try {
            val parser = ExprParser(normalized)
            val resultValue = parser.parse()
            if (resultValue.isNaN() || resultValue.isInfinite()) {
                EvalResult.Error(ERROR_DIV_ZERO)
            } else {
                EvalResult.Success(resultValue, formatResult(resultValue))
            }
        } catch (e: ArithmeticException) {
            EvalResult.Error(ERROR_DIV_ZERO)
        } catch (e: IllegalArgumentException) {
            EvalResult.Error(e.message ?: ERROR_SYNTAX)
        } catch (e: Exception) {
            EvalResult.Error(ERROR_SYNTAX)
        }
    }

    /**
     * Formats a Double number to a clean, readable string without floating-point artifacts.
     */
    fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"

        // Handle very large or very small numbers using scientific notation
        if (kotlin.math.abs(value) >= 1e12 || (kotlin.math.abs(value) > 0 && kotlin.math.abs(value) < 1e-6)) {
            val symbols = DecimalFormatSymbols(Locale.US)
            val scientificFormat = DecimalFormat("0.######E0", symbols)
            return scientificFormat.format(value)
        }

        val bd = try {
            BigDecimal.valueOf(value).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
        } catch (e: Exception) {
            BigDecimal(value.toString())
        }

        val plain = bd.toPlainString()
        return if (plain == "-0") "0" else plain
    }

    /**
     * Formats an expression for display (adding spacing around operators for readability).
     */
    fun formatExpressionForDisplay(expr: String): String {
        val sb = StringBuilder()
        for (i in expr.indices) {
            val c = expr[i]
            when (c) {
                '+', '−', '×', '÷', '^' -> sb.append(" $c ")
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private class ExprParser(private val text: String) {
        private var pos = 0
        private val length = text.length

        private fun peek(): Char? = if (pos < length) text[pos] else null

        private fun advance(): Char {
            val c = text[pos]
            pos++
            return c
        }

        fun parse(): Double {
            val res = parseAddSub()
            if (pos < length) {
                throw IllegalArgumentException(ERROR_SYNTAX)
            }
            return res
        }

        // Add & Subtract
        private fun parseAddSub(): Double {
            var value = parseMulDiv()
            while (pos < length) {
                when (peek()) {
                    '+' -> {
                        advance()
                        val right = parseMulDiv()
                        value += right
                    }
                    '-' -> {
                        advance()
                        val right = parseMulDiv()
                        value -= right
                    }
                    else -> break
                }
            }
            return value
        }

        // Multiply & Divide
        private fun parseMulDiv(): Double {
            var value = parsePower()
            while (pos < length) {
                when (peek()) {
                    '*' -> {
                        advance()
                        val right = parsePower()
                        value *= right
                    }
                    '/' -> {
                        advance()
                        val right = parsePower()
                        if (right == 0.0) throw ArithmeticException(ERROR_DIV_ZERO)
                        value /= right
                    }
                    else -> break
                }
            }
            return value
        }

        // Power (^)
        private fun parsePower(): Double {
            var value = parseUnary()
            if (pos < length && peek() == '^') {
                advance()
                val exponent = parsePower() // Right-associative
                value = value.pow(exponent)
            }
            return value
        }

        // Unary: +, -, √
        private fun parseUnary(): Double {
            if (pos >= length) throw IllegalArgumentException(ERROR_SYNTAX)
            when (peek()) {
                '+' -> {
                    advance()
                    return parseUnary()
                }
                '-' -> {
                    advance()
                    return -parseUnary()
                }
                '√' -> {
                    advance()
                    val operand = parseUnary()
                    if (operand < 0) throw IllegalArgumentException(ERROR_DOMAIN)
                    return sqrt(operand)
                }
                else -> return parsePostfix()
            }
        }

        // Postfix operator: % (percentage)
        private fun parsePostfix(): Double {
            var value = parsePrimary()
            while (pos < length && peek() == '%') {
                advance()
                value /= 100.0
            }
            return value
        }

        // Primary: number or parenthesized expression
        private fun parsePrimary(): Double {
            if (pos >= length) throw IllegalArgumentException(ERROR_SYNTAX)

            val c = peek() ?: throw IllegalArgumentException(ERROR_SYNTAX)

            // Parentheses
            if (c == '(') {
                advance()
                val value = parseAddSub()
                if (pos < length && peek() == ')') {
                    advance()
                } else {
                    throw IllegalArgumentException(ERROR_SYNTAX)
                }
                return value
            }

            // Square root as function word
            if (pos + 4 <= length && text.substring(pos, pos + 4) == "sqrt") {
                pos += 4
                val operand = parsePrimary()
                if (operand < 0) throw IllegalArgumentException(ERROR_DOMAIN)
                return sqrt(operand)
            }

            // Number parsing
            val start = pos
            var hasDot = false
            while (pos < length) {
                val ch = text[pos]
                if (ch.isDigit()) {
                    pos++
                } else if (ch == '.') {
                    if (hasDot) break
                    hasDot = true
                    pos++
                } else {
                    break
                }
            }

            if (start == pos) {
                throw IllegalArgumentException(ERROR_SYNTAX)
            }

            val numStr = text.substring(start, pos)
            return numStr.toDoubleOrNull() ?: throw IllegalArgumentException(ERROR_SYNTAX)
        }
    }
}
