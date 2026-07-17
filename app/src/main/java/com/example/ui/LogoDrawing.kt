package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Gold

@Composable
fun BrandLogo(
    modifier: Modifier = Modifier,
    showSlogan: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .shadow(12.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFEFEFEF))
                    )
                )
                .border(4.dp, Gold, CircleShape)
                .border(8.dp, EmeraldGreen.copy(alpha = 0.95f), CircleShape)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val center = Offset(width / 2, height / 2)

                // Draw central cascading blood drops
                // Main droplet
                val dropPath = Path().apply {
                    moveTo(width / 2, height * 0.2f)
                    cubicTo(
                        width * 0.25f, height * 0.55f,
                        width * 0.25f, height * 0.85f,
                        width / 2, height * 0.85f
                    )
                    cubicTo(
                        width * 0.75f, height * 0.85f,
                        width * 0.75f, height * 0.55f,
                        width / 2, height * 0.2f
                    )
                    close()
                }

                drawPath(
                    path = dropPath,
                    brush = Brush.linearGradient(
                        colors = listOf(CrimsonRed, Color(0xFF880E4F)),
                        start = Offset(width / 2, height * 0.2f),
                        end = Offset(width / 2, height * 0.85f)
                    )
                )

                // White cross inside droplet (medical sign)
                val crossWidth = width * 0.08f
                val crossHeight = height * 0.25f
                
                // Horizontal bar
                drawRect(
                    color = Color.White,
                    topLeft = Offset(width / 2 - crossHeight / 2, height * 0.55f),
                    size = Size(crossHeight, crossWidth)
                )
                // Vertical bar
                drawRect(
                    color = Color.White,
                    topLeft = Offset(width / 2 - crossWidth / 2, height * 0.55f - crossHeight / 2 + crossWidth / 2),
                    size = Size(crossWidth, crossHeight)
                )

                // Smaller side droplets dripping
                drawCircle(
                    color = CrimsonRed,
                    radius = width * 0.08f,
                    center = Offset(width * 0.35f, height * 0.45f)
                )
                drawCircle(
                    color = CrimsonRed.copy(alpha = 0.85f),
                    radius = width * 0.06f,
                    center = Offset(width * 0.68f, height * 0.48f)
                )
                drawCircle(
                    color = CrimsonRed.copy(alpha = 0.9f),
                    radius = width * 0.05f,
                    center = Offset(width * 0.28f, height * 0.68f)
                )
                drawCircle(
                    color = CrimsonRed,
                    radius = width * 0.07f,
                    center = Offset(width * 0.72f, height * 0.7f)
                )

                // Gold ring accent on the inside
                drawCircle(
                    color = Gold.copy(alpha = 0.6f),
                    radius = width * 0.46f,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        if (showSlogan) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "রক্ত দিতে প্রস্তুত আমরা",
                color = CrimsonRed,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "রক্ত দিতে প্রস্তুত আমরা",
                color = EmeraldGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
