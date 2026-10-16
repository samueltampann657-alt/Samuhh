package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.components.CalculatorDisplay
import com.example.ui.components.HistorySheet
import com.example.ui.components.Keypad
import com.example.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.toastMessage) {
        val msg = uiState.toastMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.onClearToast()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    // Scientific Mode Toggle
                    IconButton(
                        onClick = { viewModel.onToggleScientific() },
                        modifier = Modifier.testTag("btn_toggle_scientific")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Functions,
                            contentDescription = "Fungsi Lanjutan",
                            tint = if (uiState.isScientificExpanded) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    // History Toggle
                    IconButton(
                        onClick = { viewModel.onToggleHistory(true) },
                        modifier = Modifier.testTag("btn_open_history")
                    ) {
                        BadgedBox(
                            badge = {
                                if (uiState.history.isNotEmpty()) {
                                    Badge(
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.History,
                                contentDescription = stringResource(R.string.history_title),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Calculator Display (weighted to give responsive flex on different screen sizes)
            CalculatorDisplay(
                expression = uiState.expression,
                previewResult = uiState.previewResult,
                errorMessage = uiState.errorMessage,
                isCalculated = uiState.isResultCalculated,
                onCopyClick = { viewModel.onCopyResult(context) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Calculator Keypad
            Keypad(
                isScientificExpanded = uiState.isScientificExpanded,
                onDigit = viewModel::onDigit,
                onOperator = viewModel::onOperator,
                onDecimal = viewModel::onDecimal,
                onParenthesis = viewModel::onParenthesis,
                onPercentage = viewModel::onPercentage,
                onToggleSign = viewModel::onToggleSign,
                onBackspace = viewModel::onBackspace,
                onClear = viewModel::onClear,
                onCalculate = viewModel::onCalculate,
                onSquareRoot = viewModel::onSquareRoot,
                onPower = { viewModel.onOperator("^") },
                onConstant = viewModel::onConstant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // History Bottom Sheet
    if (uiState.isHistoryOpen) {
        HistorySheet(
            history = uiState.history,
            onDismiss = { viewModel.onToggleHistory(false) },
            onSelectItem = { item, useExpression ->
                viewModel.onSelectHistoryItem(item, useExpression)
            },
            onClearHistory = viewModel::onClearHistory
        )
    }
}
