package com.example.wheelpicker.ui.wheel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wheelpicker.data.model.WheelOption
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun WheelCanvas(
    options: List<WheelOption>,
    rotationDegrees: Float,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val strokePx = with(density) { 3.dp.toPx() }
    val labelColor = Color.White

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = rotationDegrees }
        ) {
            val total = options.sumOf { it.weight.toLong() }.toFloat()
            val diameter = min(size.width, size.height)
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = diameter / 2f
            val inset = radius * 0.06f
            val arcSize = Size(diameter - inset * 2, diameter - inset * 2)
            val topLeft = Offset(center.x - arcSize.width / 2f, center.y - arcSize.height / 2f)

            if (total <= 0f) return@Canvas

            var startAngle = -90f
            options.forEach { option ->
                val sweep = option.weight / total * 360f
                drawArc(
                    color = Color(option.color),
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = topLeft,
                    size = arcSize,
                )
                startAngle += sweep
            }

            startAngle = -90f
            options.forEach { option ->
                val sweep = option.weight / total * 360f
                val midAngle = startAngle + sweep / 2f
                val radians = Math.toRadians(midAngle.toDouble())
                val textRadius = radius * 0.62f
                val labelCenter = Offset(
                    center.x + (cos(radians) * textRadius).toFloat(),
                    center.y + (sin(radians) * textRadius).toFloat(),
                )
                val arcWidth = (2.0 * Math.PI * textRadius * (sweep / 360.0)).toFloat()
                val maxWidth = min(radius * 0.52f, arcWidth * 0.85f)
                val baseSize = radius * 0.30f
                var fontSize = baseSize.sp
                val measured = textMeasurer.measure(
                    text = AnnotatedString(option.label),
                    style = TextStyle(color = labelColor, fontSize = fontSize, fontWeight = FontWeight.Bold),
                )
                if (measured.size.width > maxWidth) {
                    fontSize = (baseSize * (maxWidth / measured.size.width)).sp
                }
                val layout = textMeasurer.measure(
                    text = AnnotatedString(option.label),
                    style = TextStyle(color = labelColor, fontSize = fontSize, fontWeight = FontWeight.Bold),
                )
                withTransform({
                    rotate(midAngle + 90f, labelCenter)
                }) {
                    drawText(
                        textLayoutResult = layout,
                        topLeft = Offset(
                            labelCenter.x - layout.size.width / 2f,
                            labelCenter.y - layout.size.height / 2f,
                        ),
                    )
                }
                startAngle += sweep
                drawLine(
                    color = Color.White.copy(alpha = 0.85f),
                    start = center,
                    end = Offset(
                        center.x + (cos(Math.toRadians(startAngle.toDouble())) * radius * 0.94f).toFloat(),
                        center.y + (sin(Math.toRadians(startAngle.toDouble())) * radius * 0.94f).toFloat(),
                    ),
                    strokeWidth = strokePx,
                )
            }

            drawCircle(
                color = Color(0xFFFFFFFF),
                radius = radius * 0.97f,
                style = Stroke(strokePx * 1.6f),
            )
            drawCircle(
                color = Color(0xFF37474F),
                radius = radius,
                style = Stroke(strokePx * 3f),
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val base = min(size.width, size.height)
            val pointerLength = base * 0.20f
            val rootHalf = base * 0.055f
            val pointerPath = Path().apply {
                moveTo(centerX, centerY - pointerLength)
                lineTo(centerX - rootHalf, centerY + pointerLength * 0.28f)
                lineTo(centerX + rootHalf, centerY + pointerLength * 0.28f)
                close()
            }
            drawPath(path = pointerPath, color = Color(0xFFE53935))
            drawPath(
                path = pointerPath,
                color = Color.White,
                style = Stroke(width = base * 0.007f),
            )
            drawCircle(
                color = Color(0xFF37474F),
                radius = base * 0.05f,
                center = Offset(centerX, centerY),
            )
            drawCircle(
                color = Color.White,
                radius = base * 0.05f,
                center = Offset(centerX, centerY),
                style = Stroke(strokePx),
            )
        }
    }
}
