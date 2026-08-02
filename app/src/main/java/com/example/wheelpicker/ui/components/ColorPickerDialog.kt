package com.example.wheelpicker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.wheelpicker.data.model.DEFAULT_PALETTE

@Composable
fun ColorPickerDialog(
    selectedColor: Long,
    onSelect: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择颜色") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DEFAULT_PALETTE.chunked(6).forEach { rowColors ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowColors.forEach { color ->
                            val isSelected = color == selectedColor
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(color), CircleShape)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.2f),
                                        shape = CircleShape,
                                    )
                                    .clickable { onSelect(color) },
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isSelected) {
                                    Text(text = "✓", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("完成") }
        },
    )
}
