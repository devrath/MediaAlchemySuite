package com.istudio.player.ui.screens.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PlayerLoading(modifier: Modifier = Modifier) {
    // Creates a transition that runs infinitely
    val infiniteTransition = rememberInfiniteTransition(label = "infinite_loader")

    // Animates a float value from 0f to 360f and repeats indefinitely
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing)
        ),
        label = "loader_angle"
    )

    Canvas(
        modifier = modifier.size(50.dp) // You can adjust the size here
    ) {
        drawArc(
            color = Color(0xFFE50914),
            startAngle = angle,
            sweepAngle = 270f, // The length of the arc (less than 360 for a partial circle)
            useCenter = false,
            style = Stroke(
                width = 12f, // The thickness of the arc
                cap = StrokeCap.Round // Gives the arc rounded ends
            )
        )
    }
}