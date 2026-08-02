package com.example.wheelpicker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun PasswordDialog(
    error: String?,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var input by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("后台设置") },
        text = {
            Column {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("请输入后台密码") },
                    singleLine = true,
                    isError = error != null,
                    visualTransformation = PasswordVisualTransformation(),
                )
                if (error != null) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(input) }) { Text("确定") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
    )
}

@Composable
fun ResultDialog(
    label: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("抽取结果") },
        text = {
            Text(
                text = label,
                style = MaterialTheme.typography.headlineMedium,
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("知道了") }
        },
    )
}
