package com.duyvv.basecompose.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.duyvv.basecompose.presentation.ui.theme.colorGradientPrimary

fun Modifier.noAnimClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}

fun Modifier.inVisible(isVisible: Boolean) = this
    .alpha(if (isVisible) 1f else 0f)
    .scale(if (isVisible) 1f else 0f)

fun Modifier.outerShadow(
    shadowColor: Color = Color.Black.copy(alpha = 0.1f),
    shadowRadius: Dp = 20.dp,   // độ lớn của bóng (spread)
    shadowOffsetY: Dp = 3.dp,  // lệch bóng
    steps: Int = 12             // số lớp bóng mờ (tăng = blur mịn hơn)
) = this.drawBehind {
    val radiusPx = shadowRadius.toPx()
    val offsetYPx = shadowOffsetY.toPx()
    drawIntoCanvas { canvas ->
        val paint = Paint()

        for (i in 0 until steps) {
            val alphaStep = (shadowColor.alpha / steps)
            val currentAlpha = alphaStep * (steps - i)

            paint.color = shadowColor.copy(alpha = currentAlpha)

            val expand = radiusPx * (i / steps.toFloat())

            canvas.drawOval(
                left = -expand,
                top = -expand + offsetYPx,
                right = size.width + expand,
                bottom = size.height + expand + offsetYPx,
                paint = paint
            )
        }
    }
}

@Composable
fun Modifier.borderPrimary(radius: Dp, width: Dp, alpha: Float = 1f) = then(
    Modifier.border(
        brush = Brush.linearGradient(
            colors = colorGradientPrimary.map { it.copy(alpha = alpha) },
            start = Offset(0f, 0f),
            end = Offset(0f, Float.POSITIVE_INFINITY)
        ),
        width = width,
        shape = RoundedCornerShape(radius)
    )
)

@Composable
fun Modifier.backgroundPrimary(radius: Dp) = then(
    Modifier.background(
        brush = Brush.linearGradient(
            colors = colorGradientPrimary,
            start = Offset(0f, 0f),
            end = Offset(0f, Float.POSITIVE_INFINITY)
        ),
        shape = RoundedCornerShape(radius)
    )
)