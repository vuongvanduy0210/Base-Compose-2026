package com.duyvv.basecompose.presentation.common.composeview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.duyvv.basecompose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomVectorSeekbar(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    onValueChangeStarted: () -> Unit = {},
    processColor: Color = Color(0xFF3E74EC),
    processGradient: List<Color>? = null,
    trackColor: Color = Color(0xFFCBD5E0),
    thumbSize: Dp = 10.dp,
    trackHeight: Dp = 4.dp,
    thumbTouchSize: Dp = 32.dp,
    thumbIcon: Painter
) {
    val interactionSource = remember { MutableInteractionSource() }
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            interactionSource = interactionSource,
            modifier = modifier
                .height(thumbTouchSize)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        onValueChangeStarted.invoke()
                        waitForUpOrCancellation()
                    }
                },
            thumb = {
                Box(
                    modifier = Modifier.size(thumbTouchSize),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = thumbIcon,
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(thumbSize)
                    )
                }
            },
            track = { sliderState ->
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(trackHeight)
                ) {
                    val startX = 0f
                    val endX = size.width
                    val activeEndX = size.width * sliderState.value
                    drawLine(
                        color = trackColor,
                        start = Offset(startX, center.y),
                        end = Offset(endX, center.y),
                        strokeWidth = trackHeight.toPx(),
                        cap = StrokeCap.Round
                    )

                    if (sliderState.value > 0f) {
                        if (processGradient != null && processGradient.size >= 2) {
                            drawLine(
                                brush = Brush.verticalGradient(
                                    colors = processGradient,
                                ),
                                start = Offset(startX, center.y),
                                end = Offset(activeEndX, center.y),
                                strokeWidth = trackHeight.toPx(),
                                cap = StrokeCap.Round
                            )
                        } else {
                            drawLine(
                                color = processColor,
                                start = Offset(startX, center.y),
                                end = Offset(activeEndX, center.y),
                                strokeWidth = trackHeight.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSeekbar() {
    var sliderPosition by remember { mutableFloatStateOf(0.5f) }
    Box(modifier = Modifier) {
        CustomVectorSeekbar(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            thumbIcon = painterResource(R.drawable.ic_thumb_process)
        )
    }
}