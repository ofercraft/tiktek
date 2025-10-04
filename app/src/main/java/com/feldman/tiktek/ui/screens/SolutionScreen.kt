package com.feldman.tiktek.ui.screens

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import coil.compose.AsyncImage
import kotlin.math.max


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolutionScreen(
    imageUrl: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solution") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        var containerWidth by remember { mutableStateOf(1f) }
        var containerHeight by remember { mutableStateOf(1f) }

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .onSizeChanged {
                    containerWidth = it.width.toFloat()
                    containerHeight = it.height.toFloat()
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Apply zoom
                        val newScale = (scale * zoom).coerceIn(1f, 5f)

                        // Calculate scaled image dimensions relative to container
                        val scaledWidth = containerWidth * newScale
                        val scaledHeight = containerHeight * newScale

                        // Calculate pan movement and clamp it so image stays visible
                        val maxX = max(0f, (scaledWidth - containerWidth) / 2f)
                        val maxY = max(0f, (scaledHeight - containerHeight) / 2f)

                        val newOffset = offset + pan
                        val clampedX = newOffset.x.coerceIn(-maxX, maxX)
                        val clampedY = newOffset.y.coerceIn(-maxY, maxY)

                        offset = Offset(clampedX, clampedY)
                        scale = newScale
                    }
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )
    }
}