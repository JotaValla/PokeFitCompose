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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jimmy.valladares.pokefitcompose.R
import com.jimmy.valladares.pokefitcompose.presentation.navigation.BottomNavigationBar
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitComposeTheme
import com.jimmy.valladares.pokefitcompose.ui.theme.StreakColor

@Composable
fun HomeScreen(
    onNavigateToTraining: () -> Unit = {},
    onNavigateToTab: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Refrescar datos cada vez que la pantalla se hace visible
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }
    
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
        Scaffold(
            topBar = {
                PokeFitTopBar()
            },
            bottomBar = {
                BottomNavigationBar(
                    selectedTab = BottomNavItem.HOME,
                    onTabSelected = { tab -> 
                        viewModel.handleAction(HomeAction.NavigateToTab(tab))
                    }
                )
            },
            containerColor = Color(0xFF0F0F23)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Contenido principal
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Header con Pokémon - más compacto
                    PokemonHeader(
                        pokemonName = state.pokemonName,
                        selectedPokemon = state.selectedPokemon,
                        currentLevel = state.currentLevel,
                        currentExp = state.currentExp,
                        maxExp = state.maxExp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Sección Stake (Racha) - más compacta
                    StakeSection(
                        streakDays = state.streakDays,
                        weeklyProgress = state.weeklyProgress,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Estadísticas rápidas
                    QuickStatsSection(
                        todayTrainings = state.todayTrainings,
                        currentStreak = state.streakDays,
                        expToNextLevel = state.expToNextLevel,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Botón de entrenamiento
                    StartTrainingButton(
                        onClick = { viewModel.handleAction(HomeAction.StartTraining) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                }
                
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokeFitTopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.pokefit),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        actions = {
            IconButton(onClick = { /* TODO: Implementar notificaciones */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = stringResource(R.string.notifications),
                    tint = Color.White
                )
            }
            IconButton(onClick = { /* TODO: Implementar configuración */ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0F0F23)
        )
    )
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
        // Círculo con Pokémon - más compacto
        Box(
            modifier = Modifier
                .size(140.dp) // Reducido de 160dp
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
                //"file:///android_asset/eevee.gif" // Fallback por defecto
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pokemonAssetPath)
                    .placeholder(R.drawable.pokeball)
                    .error(R.drawable.pokeball)
                    .build(),
                contentDescription = pokemonName,
                modifier = Modifier.size(100.dp) // Reducido de 120dp
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp)) // Reducido de 16dp
        
        // Nombre del Pokémon
        Text(
            text = pokemonName,
            fontSize = 20.sp, // Reducido de 24sp
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(6.dp)) // Reducido de 8dp
        
        // Nivel
        Text(
            text = stringResource(R.string.level_format, currentLevel),
            fontSize = 14.sp, // Reducido de 16sp
            color = Color(0xFF9CA3AF)
        )
        
        Spacer(modifier = Modifier.height(10.dp)) // Reducido de 12dp
        
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
                text = stringResource(R.string.exp),
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
            Text(
                text = stringResource(R.string.exp_format, currentExp, maxExp),
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
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1B2E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header mejorado con gradiente visual
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icono de fuego con fondo circular
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = StreakColor.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 20.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = stringResource(R.string.training_streak),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.maintain_daily_consistency),
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Contador de días destacado
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$streakDays",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = StreakColor
                    )
                    Text(
                        text = stringResource(R.string.days),
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Barra de progreso semanal moderna
            WeeklyProgressBar(weeklyProgress = weeklyProgress)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Etiquetas de días de la semana
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                weeklyProgress.forEach { day ->
                    Text(
                        text = day.dayName,
                        fontSize = 10.sp,
                        color = if (day.isCompleted) StreakColor else Color(0xFF9CA3AF),
                        fontWeight = if (day.isCompleted) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            if (weeklyProgress.any { it.isCompleted }) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Mensaje motivacional
                val completedDays = weeklyProgress.count { it.isCompleted }
                val motivationalMessage = when {
                    completedDays >= 5 -> "¡Increíble! Casi completas la semana 💪"
                    completedDays >= 3 -> "¡Excelente progreso! Sigue así 🚀"
                    completedDays >= 1 -> "¡Buen comienzo! Mantén el ritmo ⚡"
                    else -> "¡Es hora de empezar tu racha! 🔥"
                }
                
                Text(
                    text = motivationalMessage,
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun WeeklyProgressBar(
    weeklyProgress: List<DayProgress>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        weeklyProgress.forEachIndexed { index, day ->
            val animatedHeight by animateFloatAsState(
                targetValue = if (day.isCompleted) 1f else 0.3f,
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = index * 50 // Staggered animation
                ),
                label = "height_animation"
            )
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .background(
                        color = Color(0xFF374151),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(animatedHeight)
                        .background(
                            color = if (day.isCompleted) {
                                StreakColor
                            } else {
                                Color(0xFF374151)
                            },
                            shape = RoundedCornerShape(4.dp)
                        )
                        .align(Alignment.BottomCenter)
                )
            }
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
            text = stringResource(R.string.today_stats),
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
                title = stringResource(R.string.trainings),
                value = todayTrainings.toString(),
                subtitle = stringResource(R.string.today),
                icon = Icons.Default.FitnessCenter,
                modifier = Modifier.weight(1f)
            )
            
            QuickStatCard(
                title = stringResource(R.string.current_streak),
                value = currentStreak.toString(),
                subtitle = stringResource(R.string.days),
                icon = Icons.Default.LocalFireDepartment,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        QuickStatCard(
            title = stringResource(R.string.next_level_in),
            value = expToNextLevel.toString(),
            subtitle = stringResource(R.string.exp),
            icon = Icons.Default.TrendingUp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = value,
                fontSize = 22.sp, // Reducido de 24sp
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B5CF6)
            )
            
            Text(
                text = subtitle,
                fontSize = 11.sp, // Reducido de 12sp
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
            .height(52.dp), // Reducido de 56dp
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8B5CF6)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.start_training),
                fontSize = 16.sp, // Reducido de 18sp
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
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
        pokemonName = stringResource(R.string.eevee),
        currentLevel = 8,
        currentExp = 180,
        maxExp = 240,
        streakDays = 5,
        weeklyProgress = listOf(
            DayProgress(stringResource(R.string.monday_short), true, 15),
            DayProgress(stringResource(R.string.tuesday_short), true, 20),
            DayProgress(stringResource(R.string.wednesday_short), true, 12),
            DayProgress(stringResource(R.string.thursday_short), true, 18),
            DayProgress(stringResource(R.string.friday_short), true, 10),
            DayProgress(stringResource(R.string.saturday_short), false, 0),
            DayProgress(stringResource(R.string.sunday_short), false, 0)
        ),
        todayTrainings = 2,
        expToNextLevel = 60
    )
    
    PokeFitComposeTheme {
        // Preview con estado mock sería ideal, pero por simplicidad usamos la función normal
        HomeScreen()
    }
}
