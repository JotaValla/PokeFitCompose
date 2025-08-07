package com.jimmy.valladares.pokefitcompose.presentation.training

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.jimmy.valladares.pokefitcompose.R
import com.jimmy.valladares.pokefitcompose.data.model.*
import com.jimmy.valladares.pokefitcompose.presentation.home.BottomNavItem
import com.jimmy.valladares.pokefitcompose.presentation.navigation.BottomNavigationBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrainingScreen(
    onNavigateToTab: (String) -> Unit = {},
    onNavigateToStrengthTraining: () -> Unit = {}
) {
    // Datos de ejemplo para mostrar el diseño
    val sampleWorkouts = remember { 
        listOf(
            WorkoutSession(
                id = "1",
                userId = "user1",
                exercises = listOf(
                    WorkoutExercise("Push-ups", emptyList(), 3, 3),
                    WorkoutExercise("Squats", emptyList(), 4, 4),
                    WorkoutExercise("Plancha", emptyList(), 3, 3)
                ),
                totalDurationSeconds = 2400,
                totalDurationFormatted = "40:00",
                expGained = 120,
                completedAt = System.currentTimeMillis() - 86400000, // Ayer
                workoutType = "strength_training"
            ),
            WorkoutSession(
                id = "2",
                userId = "user1", 
                exercises = listOf(
                    WorkoutExercise("Burpees", emptyList(), 3, 3),
                    WorkoutExercise("Pull-ups", emptyList(), 3, 2),
                    WorkoutExercise("Lunges", emptyList(), 4, 4)
                ),
                totalDurationSeconds = 1800,
                totalDurationFormatted = "30:00",
                expGained = 95,
                completedAt = System.currentTimeMillis() - 172800000, // Hace 2 días
                workoutType = "strength_training"
            ),
            WorkoutSession(
                id = "3",
                userId = "user1",
                exercises = listOf(
                    WorkoutExercise("Deadlifts", emptyList(), 4, 4),
                    WorkoutExercise("Bench Press", emptyList(), 4, 4),
                    WorkoutExercise("Rows", emptyList(), 3, 3)
                ),
                totalDurationSeconds = 3000,
                totalDurationFormatted = "50:00", 
                expGained = 150,
                completedAt = System.currentTimeMillis() - 345600000, // Hace 4 días
                workoutType = "strength_training"
            )
        )
    }
    
    val isLoading = remember { false }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F23),
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Header con animación
                AnimatedHeader()
            }
            
            item {
                // Botón de nuevo entrenamiento rediseñado
                EnhancedNewWorkoutButton(
                    onNavigateToStrengthTraining = onNavigateToStrengthTraining
                )
            }
            
            item {
                // Estadísticas rápidas
                QuickStatsRow(workouts = sampleWorkouts)
            }
            
            item {
                // Historial de entrenamientos innovador
                InnovativeWorkoutHistory(
                    workouts = sampleWorkouts,
                    isLoading = isLoading
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
        
        BottomNavigationBar(
            selectedTab = BottomNavItem.TRAINING,
            onTabSelected = { tab -> onNavigateToTab(tab.route) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun AnimatedHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "headerAnimation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnimation"
    )
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "💪",
                fontSize = 32.sp,
                modifier = Modifier.scale(scale)
            )
            Column {
                Text(
                    text = stringResource(R.string.training),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Tu centro de comando fitness",
                    fontSize = 14.sp,
                    color = Color(0xFF8B5CF6),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun EnhancedNewWorkoutButton(
    onNavigateToStrengthTraining: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                isPressed = true
                onNavigateToStrengthTraining()
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF8B5CF6),
                            Color(0xFFEC4899),
                            Color(0xFFF59E0B)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "🚀 Nuevo Entrenamiento",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "¡Es hora de entrenar!",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                        .padding(6.dp)
                )
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun QuickStatsRow(workouts: List<WorkoutSession>) {
    val totalWorkouts = workouts.size
    val totalTime = workouts.sumOf { it.totalDurationSeconds } / 60 // en minutos
    val totalExp = workouts.sumOf { it.expGained }
    val avgDuration = if (workouts.isNotEmpty()) totalTime / workouts.size else 0
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            QuickStatCard(
                icon = Icons.Default.FitnessCenter,
                title = "Entrenamientos",
                value = "$totalWorkouts",
                subtitle = "completados",
                color = Color(0xFF8B5CF6)
            )
        }
        item {
            QuickStatCard(
                icon = Icons.Default.Schedule,
                title = "Tiempo Total",
                value = "${totalTime}min",
                subtitle = "entrenando",
                color = Color(0xFF10B981)
            )
        }
        item {
            QuickStatCard(
                icon = Icons.Default.Star,
                title = "EXP Ganada",
                value = "$totalExp",
                subtitle = "puntos",
                color = Color(0xFFF59E0B)
            )
        }
        item {
            QuickStatCard(
                icon = Icons.Default.Timer,
                title = "Promedio",
                value = "${avgDuration}min",
                subtitle = "por sesión",
                color = Color(0xFFEC4899)
            )
        }
    }
}

@Composable
private fun QuickStatCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InnovativeWorkoutHistory(
    workouts: List<WorkoutSession>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header del historial
            WorkoutHistoryHeader(workoutCount = workouts.size)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            when {
                isLoading -> {
                    EnhancedLoadingState()
                }
                workouts.isEmpty() -> {
                    EnhancedEmptyState()
                }
                else -> {
                    InnovativeWorkoutGrid(workouts = workouts.take(6))
                }
            }
        }
    }
}

@Composable
private fun WorkoutHistoryHeader(workoutCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6),
                                Color(0xFF6366F1)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📊",
                    fontSize = 20.sp
                )
            }
            
            Column {
                Text(
                    text = "Historial de Progreso",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Tu evolución fitness",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
        
        if (workoutCount > 0) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B5CF6).copy(alpha = 0.2f)
                )
            ) {
                Text(
                    text = "$workoutCount sesiones",
                    fontSize = 12.sp,
                    color = Color(0xFF8B5CF6),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun EnhancedLoadingState() {
    val infiniteTransition = rememberInfiniteTransition(label = "loadingAnimation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color(0xFF8B5CF6),
                modifier = Modifier
                    .size(32.dp)
                    .rotate(rotation)
            )
            Text(
                text = "Cargando entrenamientos...",
                fontSize = 14.sp,
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EnhancedEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Animación del emoji
        val infiniteTransition = rememberInfiniteTransition(label = "emptyAnimation")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500),
                repeatMode = RepeatMode.Reverse
            ),
            label = "emptyScale"
        )
        
        Text(
            text = "🏃‍♂️",
            fontSize = 64.sp,
            modifier = Modifier.scale(scale)
        )
        
        Text(
            text = "¡Tu aventura fitness comienza aquí!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Completa tu primer entrenamiento y observa\ncómo se despliega tu progreso",
            fontSize = 14.sp,
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF8B5CF6).copy(alpha = 0.15f)
            )
        ) {
            Text(
                text = "🎯 ¡Dale al botón de arriba para empezar!",
                fontSize = 12.sp,
                color = Color(0xFF8B5CF6),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun InnovativeWorkoutGrid(workouts: List<WorkoutSession>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        workouts.forEachIndexed { index, workout ->
            InnovativeWorkoutCard(
                workout = workout,
                index = index,
                isLatest = index == 0
            )
        }
        
        if (workouts.size > 6) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF374151).copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "👀 Y ${workouts.size - 6} entrenamientos más en tu historial",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun InnovativeWorkoutCard(
    workout: WorkoutSession,
    index: Int,
    isLatest: Boolean
) {
    val workoutColors = listOf(
        Color(0xFF8B5CF6) to Color(0xFF6366F1),
        Color(0xFF10B981) to Color(0xFF059669),
        Color(0xFFF59E0B) to Color(0xFFD97706),
        Color(0xFFEC4899) to Color(0xFFDB2777),
        Color(0xFF6366F1) to Color(0xFF4F46E5),
        Color(0xFF8B5CF6) to Color(0xFF7C3AED)
    )
    
    val (primaryColor, secondaryColor) = workoutColors[index % workoutColors.size]
    
    var isExpanded by remember { mutableStateOf(false) }
    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) 160.dp else 100.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardHeight"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = if (isLatest) {
            BorderStroke(2.dp, primaryColor.copy(alpha = 0.5f))
        } else {
            BorderStroke(1.dp, primaryColor.copy(alpha = 0.2f))
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.1f),
                            secondaryColor.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                // Header de la card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Indicador de workout
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(primaryColor, secondaryColor)
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getWorkoutEmoji(index),
                                fontSize = 18.sp
                            )
                        }
                        
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = formatInnovativeDate(workout.completedAt),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                
                                if (isLatest) {
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF10B981).copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text(
                                            text = "✨ Reciente",
                                            fontSize = 10.sp,
                                            color = Color(0xFF10B981),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            
                            Text(
                                text = "${workout.exercises.size} ejercicios • ${workout.exercises.sumOf { it.completedSets }} series",
                                fontSize = 12.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                    
                    // Stats rápidas
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = workout.totalDurationFormatted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                        
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF59E0B).copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = "+${workout.expGained} EXP",
                                fontSize = 11.sp,
                                color = Color(0xFFF59E0B),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                
                // Contenido expandible
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = primaryColor.copy(alpha = 0.3f)
                        )
                        
                        // Lista de ejercicios
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(workout.exercises) { exercise ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = primaryColor.copy(alpha = 0.15f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = exercise.name,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${exercise.completedSets}/${exercise.totalSets}",
                                            fontSize = 9.sp,
                                            color = primaryColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Indicador de expandible
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = primaryColor.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp)
            )
        }
    }
}

private fun getWorkoutEmoji(index: Int): String {
    val emojis = listOf("💪", "🔥", "⚡", "🏋️", "🚀", "⭐")
    return emojis[index % emojis.size]
}

private fun formatInnovativeDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val hours = diff / (60 * 60 * 1000)
    val days = diff / (24 * 60 * 60 * 1000)
    
    return when {
        hours < 24 -> {
            when {
                hours < 1 -> "Hace unos minutos"
                hours == 1L -> "Hace 1 hora"
                else -> "Hace ${hours}h"
            }
        }
        days == 1L -> "Ayer"
        days < 7L -> {
            val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
            formatter.format(Date(timestamp))
        }
        else -> {
            val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
            formatter.format(Date(timestamp))
        }
    }
}
