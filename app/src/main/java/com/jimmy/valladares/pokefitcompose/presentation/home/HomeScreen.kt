package com.jimmy.valladares.pokefitcompose.presentation.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.jimmy.valladares.pokefitcompose.presentation.navigation.BottomNavigationBar
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitComposeTheme

@Composable
fun HomeScreen(
    onNavigateToTraining: () -> Unit = {},
    onNavigateToTab: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.NavigateToTraining -> onNavigateToTraining()
                is HomeEvent.NavigateToTab -> onNavigateToTab(event.route)
                is HomeEvent.ShowLevelUp -> {
                    // TODO: Mostrar animación de level up
                }
                is HomeEvent.ShowError -> {
                    // TODO: Mostrar error en snackbar
                }
                else -> {}
            }
        }
    }

    PokeFitComposeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F0F23)) // Fondo sólido púrpura oscuro
        ) {
            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp) // Espacio para bottom navigation
                    .verticalScroll(rememberScrollState())
            ) {
                // Header con Pokémon
                PokemonHeader(
                    pokemonName = state.pokemonName,
                    selectedPokemon = state.selectedPokemon,
                    currentLevel = state.currentLevel,
                    currentExp = state.currentExp,
                    maxExp = state.maxExp,
                    modifier = Modifier.padding(24.dp)
                )
                
                // Sección Stake (Racha)
                StakeSection(
                    streakDays = state.streakDays,
                    weeklyProgress = state.weeklyProgress,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Estadísticas rápidas
                QuickStatsSection(
                    todayTrainings = state.todayTrainings,
                    currentStreak = state.streakDays,
                    expToNextLevel = state.expToNextLevel,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Botón de entrenamiento
                StartTrainingButton(
                    onClick = { viewModel.handleAction(HomeAction.StartTraining) },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Bottom Navigation
            BottomNavigationBar(
                selectedTab = BottomNavItem.HOME,
                onTabSelected = { tab -> 
                    viewModel.handleAction(HomeAction.NavigateToTab(tab))
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            
            // Loading overlay
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF8B5CF6)
                    )
                }
            }
        }
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
                .size(160.dp)
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
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${selectedPokemon}.gif")
                    .build(),
                contentDescription = pokemonName,
                modifier = Modifier.size(120.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Nombre del Pokémon
        Text(
            text = pokemonName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Nivel
        Text(
            text = "Nivel $currentLevel",
            fontSize = 16.sp,
            color = Color(0xFF9CA3AF)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
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
    val progress = currentExp.toFloat() / maxExp.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress"
    )
    
    Column(modifier = modifier) {
        // Información de EXP
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
        
        Spacer(modifier = Modifier.height(8.dp))
        
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
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(8.dp)
                    .background(
                        color = Color(0xFF8B5CF6),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
private fun StakeSection(
    streakDays: Int,
    weeklyProgress: List<DayProgress>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Stake 🔥",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$streakDays días",
                fontSize = 14.sp,
                color = Color(0xFF10B981)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Pokeballs de la semana
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(weeklyProgress) { dayProgress ->
                StakeDayItem(dayProgress = dayProgress)
            }
        }
    }
}

@Composable
private fun StakeDayItem(
    dayProgress: DayProgress,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Pokeball
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (dayProgress.isCompleted) Color(0xFF8B5CF6) else Color(0xFF374151),
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = if (dayProgress.isCompleted) Color(0xFFA855F7) else Color(0xFF4B5563),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Simular pokeball con un círculo interno
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (dayProgress.isCompleted) Color.White else Color(0xFF6B7280),
                        shape = CircleShape
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Día de la semana
        Text(
            text = dayProgress.dayName,
            fontSize = 12.sp,
            color = Color(0xFF9CA3AF)
        )
        
        // EXP ganada
        if (dayProgress.isCompleted && dayProgress.expGained > 0) {
            Text(
                text = "+${dayProgress.expGained} exp",
                fontSize = 10.sp,
                color = Color(0xFF10B981)
            )
        }
    }
}

@Composable
private fun QuickStatsSection(
    todayTrainings: Int,
    currentStreak: Int,
    expToNextLevel: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Estadísticas de hoy",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickStatCard(
                title = "Entrenamientos",
                value = todayTrainings.toString(),
                subtitle = "hoy",
                modifier = Modifier.weight(1f)
            )
            
            QuickStatCard(
                title = "Racha actual",
                value = currentStreak.toString(),
                subtitle = "días",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        QuickStatCard(
            title = "Próximo nivel en",
            value = expToNextLevel.toString(),
            subtitle = "exp",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1B2E)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5CF6)
            )
            
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun StartTrainingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8B5CF6)
        )
    ) {
        Text(
            text = "🏋️ Iniciar Entrenamiento",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PokeFitComposeTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenWithStreakPreview() {
    val mockState = HomeState(
        selectedPokemon = "eevee",
        pokemonName = "Eevee",
        currentLevel = 8,
        currentExp = 180,
        maxExp = 240,
        streakDays = 5,
        weeklyProgress = listOf(
            DayProgress("Lun", true, 15),
            DayProgress("Mar", true, 20),
            DayProgress("Mie", true, 12),
            DayProgress("Jue", true, 18),
            DayProgress("Vie", true, 10),
            DayProgress("Sab", false, 0),
            DayProgress("Dom", false, 0)
        ),
        todayTrainings = 2,
        expToNextLevel = 60
    )
    
    PokeFitComposeTheme {
        // Preview con estado mock sería ideal, pero por simplicidad usamos la función normal
        HomeScreen()
    }
}
