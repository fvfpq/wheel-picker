package com.example.wheelpicker.ui.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.wheelpicker.data.model.MAX_OPTIONS
import com.example.wheelpicker.data.model.MIN_OPTIONS
import com.example.wheelpicker.data.model.WheelOption
import com.example.wheelpicker.data.model.defaultOptions
import com.example.wheelpicker.data.model.nextAutoColor
import com.example.wheelpicker.ui.components.ColorPickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    viewModel: EditViewModel,
    onBack: () -> Unit,
) {
    val savedOptions by viewModel.options.collectAsState()
    val draft = remember(savedOptions) { savedOptions.toMutableStateList() }
    var colorPickerForId by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    fun trySave(): Boolean {
        validationError = null
        if (draft.any { it.label.isBlank() }) {
            validationError = "选项文字不能为空"
            return false
        }
        viewModel.save(draft.map { it.normalized() })
        return true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑选项") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (trySave()) onBack()
                    }) { Text("保存") }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(draft, key = { _, item -> item.id }) { index, item ->
                OptionEditorRow(
                    option = item,
                    canRemove = draft.size > MIN_OPTIONS,
                    onLabelChange = { newLabel -> draft[index] = item.copy(label = newLabel) },
                    onWeightChange = { newWeight -> draft[index] = item.copy(weight = newWeight) },
                    onColorClick = { colorPickerForId = item.id },
                    onRemove = { draft.removeAt(index) },
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = {
                            draft.add(
                                WheelOption(
                                    label = "新选项",
                                    color = nextAutoColor(draft),
                                )
                            )
                        },
                        enabled = draft.size < MAX_OPTIONS,
                    ) { Text("添加选项") }

                    TextButton(onClick = {
                        draft.clear()
                        draft.addAll(defaultOptions())
                    }) { Text("恢复默认") }
                }
            }

            if (draft.size >= MAX_OPTIONS) {
                item {
                    Text(
                        text = "选项数量已达上限 $MAX_OPTIONS",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            validationError?.let { error ->
                item {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    colorPickerForId?.let { id ->
        val current = draft.firstOrNull { it.id == id }
        if (current != null) {
            ColorPickerDialog(
                selectedColor = current.color,
                onSelect = { color ->
                    draft.replaceAll { if (it.id == id) it.copy(color = color) else it }
                },
                onDismiss = { colorPickerForId = null },
            )
        }
    }
}

@Composable
private fun OptionEditorRow(
    option: WheelOption,
    canRemove: Boolean,
    onLabelChange: (String) -> Unit,
    onWeightChange: (Int) -> Unit,
    onColorClick: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(option.color), RoundedCornerShape(8.dp))
                .clickable(onClick = onColorClick),
        )
        OutlinedTextField(
            value = option.label,
            onValueChange = onLabelChange,
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        WeightField(weight = option.weight, onWeightChange = onWeightChange)
        IconButton(onClick = onRemove, enabled = canRemove) {
            Icon(Icons.Outlined.Delete, contentDescription = "删除")
        }
    }
}

@Composable
private fun WeightField(
    weight: Int,
    onWeightChange: (Int) -> Unit,
) {
    var text by remember(weight) { mutableStateOf(weight.toString()) }
    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            text = newText.filter { it.isDigit() }.take(3)
            onWeightChange(text.toIntOrNull() ?: 1)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.width(72.dp),
    )
}
