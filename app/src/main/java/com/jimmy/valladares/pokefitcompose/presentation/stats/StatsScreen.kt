package com.jimmy.valladares.pokefitcompose.presentation.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jimmy.valladares.pokefitcompose.R
import com.jimmy.valladares.pokefitcompose.presentation.home.BottomNavItem
import com.jimmy.valladares.pokefitcompose.presentation.navigation.BottomNavigationBar
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientEnd
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientStart
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitComposeTheme

@Composable
fun StatsScreen(
    onNavigateToTab: (String) -> Unit = {},
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 60.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header con título
            item {
                Text(
                    text = stringResource(R.string.stats_title),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            // Pokemon Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.3f)
                    )
                ) {
                    PokemonHeader(
                        pokemonName = state.pokemonName,
                        selectedPokemon = state.selectedPokemon,
                        currentLevel = state.currentLevel,
                        currentExp = state.currentExp,
                        maxExp = state.maxExp,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
            
            // Estadísticas de Actividad (4 recuadros en un contenedor)
            item {
                ActivityStatsCard(
                    averageDaysPerWeek = state.averageDaysPerWeek,
                    maxStreak = state.maxStreak,
                    averageMinutes = state.averageMinutes,
                    totalWorkouts = state.totalWorkouts
                )
            }
            
            // Experiencia de la última semana
            item {
                WeeklyExpCard(
                    weeklyExp = state.weeklyExp,
                    previousWeekExp = state.previousWeekExp
                )
            }
            
            // Experiencia total acumulada
            item {
                TotalExpCard(
                    totalExp = state.totalExp,
                    totalLevelsGained = state.totalLevelsGained
                )
            }
        }
        
        // Bottom Navigation
        BottomNavigationBar(
            selectedTab = BottomNavItem.STATS,
            onTabSelected = { tab ->
                onNavigateToTab(tab.route)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun PokemonHeader(
    pokemonName: String,
    selectedPokemon: String,
    currentLevel: Int,
    currentExp: Int,
    maxExp: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Círculo con Pokémon
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(
                    color = Color.Black,
                    shape = CircleShape
                )
                .border(
                    width = 3.dp,
                    color = Color(0xFF8B5CF6),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val pokemonAssetPath = if (selectedPokemon.isNotBlank()) {
                "file:///android_asset/${selectedPokemon}.gif"
            } else {
                "file:///android_asset/eevee.gif" // Fallback por defecto
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pokemonAssetPath)
                    .placeholder(R.drawable.pokeball)
                    .error(R.drawable.pokeball)
                    .build(),
                contentDescription = pokemonName,
                modifier = Modifier.size(100.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Nombre del Pokémon
        Text(
            text = pokemonName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // Nivel
        Text(
            text = stringResource(R.string.level_format, currentLevel),
            fontSize = 14.sp,
            color = Color(0xFF9CA3AF)
        )
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Barra de progreso
        PokemonProgressBar(
            currentExp = currentExp,
            maxExp = maxExp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PokemonProgressBar(
    currentExp: Int,
    maxExp: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Texto de experiencia
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "EXP",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
            Text(
                text = "$currentExp / $maxExp",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Barra de progreso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color = Color(0xFF374151),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            val progress = (currentExp.toFloat() / maxExp.toFloat()).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6),
                                Color(0xFFA855F7)
                            )
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
private fun ActivityStatsCard(
    averageDaysPerWeek: Float,
    maxStreak: Int,
    averageMinutes: Int,
    totalWorkouts: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Resumen de Actividad",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Grid de 2x2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Días promedio por semana
                StatItem(
                    title = "Días/Semana",
                    value = String.format("%.1f", averageDaysPerWeek),
                    icon = Icons.Default.CalendarToday,
                    modifier = Modifier.weight(1f)
                )
                
                // Racha máxima
                StatItem(
                    title = "Racha Máxima",
                    value = "$maxStreak días",
                    icon = Icons.Default.LocalFireDepartment,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Minutos promedio
                StatItem(
                    title = "Min Promedio",
                    value = "$averageMinutes min",
                    icon = Icons.Default.AccessTime,
                    modifier = Modifier.weight(1f)
                )
                
                // Total de entrenamientos
                StatItem(
                    title = "Entrenamientos",
                    value = "$totalWorkouts",
                    icon = Icons.Default.FitnessCenter,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeeklyExpCard(
    weeklyExp: Int,
    previousWeekExp: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Esta Semana",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Progreso semanal",
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "$weeklyExp EXP",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5CF6),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val difference = weeklyExp - previousWeekExp
            val isPositive = difference >= 0
            val percentage = if (previousWeekExp > 0) {
                (difference.toFloat() / previousWeekExp.toFloat() * 100).toInt()
            } else {
                0
            }
            
            Text(
                text = if (isPositive) {
                    "+$difference EXP (+$percentage%) vs semana anterior"
                } else {
                    "$difference EXP ($percentage%) vs semana anterior"
                },
                fontSize = 12.sp,
                color = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TotalExpCard(
    totalExp: Int,
    totalLevelsGained: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Experiencia Total",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFB17CE8),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = String.format("%,d EXP", totalExp),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFCD34D),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$totalLevelsGained niveles ganados en total",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun UserHeader(
    userName: String,
    userAge: Int,
    userWeight: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Avatar del usuario con GIF
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = Color.Black,
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFF10B981),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/User.gif")
                    .placeholder(R.drawable.pokeball)
                    .error(R.drawable.pokeball)
                    .build(),
                contentDescription = "Usuario",
                modifier = Modifier.size(60.dp)
            )
        }
        
        // Información del usuario
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (userName.isNotBlank()) userName else "Usuario",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            if (userAge > 0) {
                Text(
                    text = "$userAge años",
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
            
            if (userWeight > 0) {
                Text(
                    text = "${String.format("%.1f", userWeight)} kg",
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
        
        // Icono de perfil
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👤",
                fontSize = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatsScreenPreview() {
    PokeFitComposeTheme {
        StatsScreen()
    }
}
