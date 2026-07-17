package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

// --- 1. Custom Canvas Bar Chart for Area Stats ---
@Composable
fun StatsBarChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier,
    barColor: Color = CrimsonRed,
    accentColor: Color = Gold
) {
    val maxCount = (data.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "এলাকা-ভিত্তিক রক্তদাতার পরিসংখ্যান (Area Statistics)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val width = size.width
                val height = size.height
                val barSpacing = width / (data.size * 1.5f)
                val barWidth = barSpacing * 0.7f
                val chartHeight = height - 40.dp.toPx()

                // Draw background lines
                for (i in 0..3) {
                    val y = chartHeight * (i / 3f)
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw bars
                data.forEachIndexed { index, pair ->
                    val x = index * barSpacing + barSpacing * 0.3f
                    val barHeight = (pair.second.toFloat() / maxCount) * chartHeight
                    val y = chartHeight - barHeight

                    // Draw bar background shadow
                    drawRect(
                        color = Color.LightGray.copy(alpha = 0.15f),
                        topLeft = Offset(x, 0f),
                        size = Size(barWidth, chartHeight)
                    )

                    // Draw bar gradient
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(barColor, barColor.copy(alpha = 0.7f))
                        ),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight)
                    )

                    // Draw top highlight line
                    drawRect(
                        color = accentColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, 4.dp.toPx())
                    )

                    // Draw count text above bar
                    // Using drawing scope is low-level, we can omit text coordinates or draw custom labels below
                }
            }

            // Labels Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEach { pair ->
                    Text(
                        text = pair.first.take(5),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// --- 2. Custom Canvas Pie/Doughnut Chart for Blood Groups ---
@Composable
fun StatsPieChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.second }.coerceAtLeast(1)
    val colors = listOf(
        CrimsonRed, EmeraldGreen, Gold, Color(0xFFC2185B),
        Color(0xFF303F9F), Color(0xFF00796B), Color(0xFFE64A19), Color(0xFF5D4037)
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "গ্রুপ-ভিত্তিক রক্তের প্রাপ্যতা (Group Distribution)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Doughnut chart Canvas
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .weight(1.2f),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        var startAngle = 0f
                        data.forEachIndexed { index, pair ->
                            val sweepAngle = (pair.second.toFloat() / total) * 360f
                            drawArc(
                                color = colors[index % colors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                size = Size(size.width, size.height),
                                style = Stroke(width = 24.dp.toPx())
                            )
                            startAngle += sweepAngle
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$total",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "রক্তদাতা",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Legend List
                Column(
                    modifier = Modifier.weight(1.8f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    data.forEachIndexed { index, pair ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(colors[index % colors.size])
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${pair.first}: ${pair.second} জন (${String.format("%.1f", (pair.second.toFloat() / total) * 100)}%)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 3. Glowing Location-Based Donor Mapping Radar ---
@Composable
fun RadarMapAnimation(
    modifier: Modifier = Modifier,
    matchingDonorsCount: Int = 5,
    onPointClicked: (String) -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    // Pre-calculated points inside the radar circle representing registered donors
    val donorPoints = remember {
        listOf(
            RadarPoint("আরিফ রহমান", Offset(0.35f, 0.3f), "A+"),
            RadarPoint("মেহেদী হাসান", Offset(0.65f, 0.4f), "AB+"),
            RadarPoint("ফারজানা আক্তার", Offset(0.25f, 0.6f), "O-"),
            RadarPoint("তানভীর আহমেদ", Offset(0.55f, 0.75f), "B-"),
            RadarPoint("কামরুল ইসলাম", Offset(0.42f, 0.52f), "A-"),
            RadarPoint("সৌরভ নাগ", Offset(0.5f, 0.5f), "O+") // Center base
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkBackground)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2, height / 2)
            val maxRadius = (width.coerceAtMost(height) / 2) * 0.95f

            // 1. Draw radar background circles
            for (i in 1..4) {
                drawCircle(
                    color = EmeraldGreen.copy(alpha = 0.15f * i),
                    radius = maxRadius * (i / 4f),
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // 2. Draw cross hairs
            drawLine(
                color = EmeraldGreen.copy(alpha = 0.3f),
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = EmeraldGreen.copy(alpha = 0.3f),
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1.dp.toPx()
            )

            // 3. Draw expanding pulsing ring
            drawCircle(
                color = EmeraldGreen.copy(alpha = 1f - pulseScale),
                radius = maxRadius * pulseScale,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // 4. Draw sweeping radar arm
            val armLength = maxRadius
            val angleRad = Math.toRadians(sweepAngle.toDouble())
            val armEnd = Offset(
                x = (center.x + armLength * cos(angleRad)).toFloat(),
                y = (center.y + armLength * sin(angleRad)).toFloat()
            )

            drawLine(
                color = EmeraldGreen.copy(alpha = 0.8f),
                start = center,
                end = armEnd,
                strokeWidth = 3.dp.toPx()
            )

            // Draw fading sweep gradient trace
            // We can approximate with beautiful circle blobs
            drawCircle(
                color = EmeraldGreen.copy(alpha = 0.15f),
                radius = maxRadius * 0.8f,
                center = center
            )

            // 5. Draw donor points
            donorPoints.take(matchingDonorsCount).forEach { point ->
                val px = point.offsetRatio.x * width
                val py = point.offsetRatio.y * height
                val targetOffset = Offset(px, py)

                // Glowing pulse for each donor
                drawCircle(
                    color = CrimsonRed.copy(alpha = 0.4f),
                    radius = 12.dp.toPx(),
                    center = targetOffset
                )
                drawCircle(
                    color = Gold,
                    radius = 5.dp.toPx(),
                    center = targetOffset
                )
            }
        }

        // Clickable interactive layer overlay for points
        Box(modifier = Modifier.fillMaxSize()) {
            donorPoints.take(matchingDonorsCount).forEach { point ->
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .offset(
                            x = (point.offsetRatio.x * 280).dp,
                            y = (point.offsetRatio.y * 180).dp
                        )
                        .clip(CircleShape)
                        .clickable { onPointClicked(point.name) }
                )
            }
        }

        // Radar Glowing Status Text
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(EmeraldGreenLight)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "লাইভ ডোনার রাডার: কুমিল্লা সদর (Scanning Live...)",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class RadarPoint(
    val name: String,
    val offsetRatio: Offset,
    val bgGroup: String
)

// --- 4. Premium Stat Card with Golden Accents ---
@Composable
fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = CrimsonRed
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Diagonal decoration brush
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    moveTo(size.width * 0.7f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(size.width * 0.4f, size.height)
                    close()
                }
                drawPath(path = path, color = Color.White.copy(alpha = 0.08f))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = value,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
