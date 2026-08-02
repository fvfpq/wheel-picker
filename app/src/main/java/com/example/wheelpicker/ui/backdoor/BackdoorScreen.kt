package com.example.wheelpicker.ui.backdoor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.wheelpicker.data.model.WheelOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackdoorScreen(
    viewModel: BackdoorViewModel,
    onBack: () -> Unit,
) {
    val config by viewModel.config.collectAsState()
    val queue = config.forcedQueue
    val optionById = config.options.associateBy { it.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("后台控制") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(onClick = onBack) { Text("确定") }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (queue.isNotEmpty()) "已指定 ${queue.size} 轮，每次旋转依次生效" else "当前为随机模式",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.clearAll() },
                        enabled = queue.isNotEmpty(),
                    ) { Text("清空全部指定") }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (queue.isNotEmpty()) {
                Text(
                    text = "指定队列（第 1 次 → 第 ${queue.size} 次）",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                ForcedQueueList(
                    queue = queue,
                    optionById = optionById,
                    onRemove = viewModel::removeAt,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "追加指定（点击选项加入队列末尾）",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(config.options, key = { it.id }) { option ->
                    OptionSelectRow(
                        option = option,
                        isForced = queue.contains(option.id),
                        onClick = { viewModel.enqueue(option.id) },
                    )
                }
            }

            PasswordChangeCard(viewModel = viewModel)
        }
    }
}

@Composable
private fun ForcedQueueList(
    queue: List<String>,
    optionById: Map<String, WheelOption>,
    onRemove: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            itemsIndexed(queue) { index, optionId ->
                val option = optionById[optionId]
                if (option != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "第 ${index + 1} 次",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(option.color), RoundedCornerShape(4.dp)),
                        )
                        Text(text = option.label, modifier = Modifier.weight(1f))
                        IconButton(onClick = { onRemove(optionId) }, modifier = Modifier.size(24.dp)) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "移除第 ${index + 1} 次指定",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionSelectRow(
    option: WheelOption,
    isForced: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isForced) 2.dp else 1.dp,
                color = if (isForced) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color(option.color), RoundedCornerShape(6.dp)),
        )
        Text(text = option.label, modifier = Modifier.weight(1f))
        Text(
            text = "权重 ${option.weight}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (isForced) {
            Icon(
                Icons.Outlined.Check,
                contentDescription = "已指定",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun PasswordChangeCard(viewModel: BackdoorViewModel) {
    var password by remember { mutableStateOf("") }
    var savedMessage by remember { mutableStateOf<String?>(null) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "修改后台密码", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; savedMessage = null },
                label = { Text("新密码") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    if (password.isNotBlank()) {
                        viewModel.updatePassword(password)
                        password = ""
                        savedMessage = "密码已更新"
                    }
                },
            ) { Text("保存密码") }
            savedMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
