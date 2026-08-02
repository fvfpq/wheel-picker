package com.example.wheelpicker.ui.wheel

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wheelpicker.ui.components.PasswordDialog
import com.example.wheelpicker.ui.components.ResultDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheelScreen(
    viewModel: WheelViewModel,
    onOpenEdit: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenBackdoor: () -> Unit,
) {
    val config by viewModel.config.collectAsState()
    val isSpinning by viewModel.isSpinning.collectAsState()
    val resultLabel by viewModel.resultLabel.collectAsState()
    val showPasswordPrompt by viewModel.showPasswordPrompt.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val rotation by viewModel.rotation.asState()
    val spinRequest by viewModel.spinRequest.collectAsState()

    LaunchedEffect(spinRequest) {
        if (spinRequest > 0) {
            viewModel.performSpin()
        }
    }

    Scaffold(
        topBar = {
            Box {
                TopAppBar(
                    title = { Text("转盘选择") },
                    navigationIcon = {
                        IconButton(onClick = onOpenEdit) {
                            Icon(Icons.Outlined.Edit, contentDescription = "编辑选项")
                        }
                    },
                    actions = {
                        IconButton(onClick = onOpenHistory) {
                            Icon(Icons.Outlined.History, contentDescription = "历史记录")
                        }
                    },
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(horizontal = 72.dp)
                        .pointerInput(Unit) {
                            detectTapGestures { viewModel.onTopBarTap() }
                        },
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                if (config.options.size >= 2) {
                    WheelCanvas(
                        options = config.options,
                        rotationDegrees = rotation,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(
                        text = "至少需要 2 个选项，请先编辑",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.requestSpin() },
                enabled = !isSpinning && config.options.size >= 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Text(
                    text = if (isSpinning) "旋转中…" else "开始旋转",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }

    if (showPasswordPrompt) {
        PasswordDialog(
            error = passwordError,
            onConfirm = { input ->
                if (viewModel.verifyPassword(input)) {
                    viewModel.dismissPasswordPrompt()
                    onOpenBackdoor()
                } else {
                    viewModel.reportPasswordError()
                }
            },
            onDismiss = { viewModel.dismissPasswordPrompt() },
        )
    }

    resultLabel?.let { label ->
        ResultDialog(label = label, onDismiss = { viewModel.clearResult() })
    }
}
