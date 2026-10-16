package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.CalculatorEvaluator
import com.example.util.EvalResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Kalkulator", appName)
    }

    @Test
    fun `evaluator basic operations`() {
        val resAdd = CalculatorEvaluator.evaluate("12 + 8")
        assertTrue(resAdd is EvalResult.Success)
        assertEquals("20", (resAdd as EvalResult.Success).formatted)

        val resSub = CalculatorEvaluator.evaluate("50 − 15")
        assertTrue(resSub is EvalResult.Success)
        assertEquals("35", (resSub as EvalResult.Success).formatted)

        val resMul = CalculatorEvaluator.evaluate("6 × 7")
        assertTrue(resMul is EvalResult.Success)
        assertEquals("42", (resMul as EvalResult.Success).formatted)

        val resDiv = CalculatorEvaluator.evaluate("100 ÷ 4")
        assertTrue(resDiv is EvalResult.Success)
        assertEquals("25", (resDiv as EvalResult.Success).formatted)
    }

    @Test
    fun `evaluator percentage and precedence`() {
        val res = CalculatorEvaluator.evaluate("2 + 3 × 4")
        assertTrue(res is EvalResult.Success)
        assertEquals("14", (res as EvalResult.Success).formatted)

        val resPercent = CalculatorEvaluator.evaluate("50%")
        assertTrue(resPercent is EvalResult.Success)
        assertEquals("0.5", (resPercent as EvalResult.Success).formatted)
    }

    @Test
    fun `evaluator division by zero`() {
        val res = CalculatorEvaluator.evaluate("10 ÷ 0")
        assertTrue(res is EvalResult.Error)
        assertEquals("Tidak dapat membagi dengan 0", (res as EvalResult.Error).message)
    }
}
