package com.jimmy.valladares.pokefitcompose.presentation.strength_training

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.jimmy.valladares.pokefitcompose.R
import com.jimmy.valladares.pokefitcompose.data.model.WorkoutSession
import com.jimmy.valladares.pokefitcompose.presentation.home.BottomNavItem
import com.jimmy.valladares.pokefitcompose.presentation.navigation.BottomNavigationBar
import com.jimmy.valladares.pokefitcompose.presentation.strength_training.components.ExperienceGainedDialog
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientEnd
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientStart
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitComposeTheme
import com.jimmy.valladares.pokefitcompose.ui.theme.StreakColor
import kotlin.collections.take

@Composable
fun StrengthTrainingScreen(
    onNavigateToTab: (String) -> Unit = {},
    viewModel: StrengthTrainingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Estados para el diálogo de experiencia
    var showExperienceDialog by remember { mutableStateOf(false) }
    var experienceData by remember { 
        mutableStateOf<StrengthTrainingEvent.ExperienceGained?>(null) 
    }
    
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is StrengthTrainingEvent.TrainingCompleted -> {
                    // Handle training completion
                }
                is StrengthTrainingEvent.ShowMessage -> {
                    // Show snackbar or toast
                }
                is StrengthTrainingEvent.WorkoutSaved -> {
                    // Handle workout saved successfully
                    // You could show a different message or navigate somewhere
                }
                is StrengthTrainingEvent.ExperienceGained -> {
                    // Mostrar diálogo de experiencia ganada
                    experienceData = event
                    showExperienceDialog = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F23))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar con Timer de Entrenamiento y Pokémon (solo cuando entrenamiento iniciado)
            if (state.isTrainingStarted) {
                TrainingTopBar(state = state)
            }
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp), // Espacio para bottom navigation
                contentPadding = PaddingValues(
                    top = if (state.isTrainingStarted) 80.dp else 60.dp, // Más espacio cuando hay topbar
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Menos espacio entre elementos
            ) {
                // Rest Timer Section (pequeño y compacto)
                if (state.showRestTimer) {
                    item {
                        CompactRestTimer(
                            state = state,
                            onAction = viewModel::onAction
                        )
                    }
                }

                // Header Section - Configuración de Ejercicios
                item {
                    HeaderSection(
                        state = state,
                        onAction = viewModel::onAction
                    )
                }

                // Workout History Section (only visible when not training)
                if (!state.isTrainingStarted) {
                    item {
                        WorkoutHistorySection(
                            state = state,
                            onAction = viewModel::onAction
                        )
                    }
                }

                // Exercise Table Section
                if (state.isTrainingStarted) {
                    item {
                        ExerciseTableSection(
                            state = state,
                            onAction = viewModel::onAction
                        )
                    }
                }

                // Exercise Summary (only visible when there are completed exercises)
                if (state.completedExercises.isNotEmpty() && state.isTrainingStarted) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.completed_exercises),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                state.completedExercises.forEach { exercise ->
                                    val sets = state.exerciseHistory[exercise]?.count { it.isCompleted } ?: 0
                                    Text(
                                        text = stringResource(R.string.exercise_series_format, exercise, sets),
                                        fontSize = 14.sp,
                                        color = Color(0xFFE8E3FF),
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Finish Training Button
                if (state.isFinishVisible) {
                    item {
                        Button(
                            onClick = { 
                                viewModel.onAction(StrengthTrainingAction.FinishTraining)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8B5CF6)
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.finish_training),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Floating Rest Timer (pequeño y menos intrusivo cuando no es el foco principal)
        if (state.isRestTimerActive && !state.showRestTimer) {
            FloatingRestTimer(
                state = state,
                onAction = viewModel::onAction,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
        }

        // Bottom Navigation
        BottomNavigationBar(
            selectedTab = BottomNavItem.TRAINING,
            onTabSelected = { tab -> onNavigateToTab(tab.route) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        // Diálogo de experiencia ganada
        if (showExperienceDialog && experienceData != null) {
            ExperienceGainedDialog(
                expGained = experienceData!!.expGained,
                leveledUp = experienceData!!.leveledUp,
                newLevel = experienceData!!.newLevel,
                breakdown = experienceData!!.breakdown,
                onDismiss = {
                    showExperienceDialog = false
                    experienceData = null
                }
            )
        }
    }
}

@Composable
private fun TrainingTopBar(
    state: StrengthTrainingState
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(top = 24.dp), // Espacio para la barra del sistema
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Pokémon pequeño
            AnimatedVisibility(
                visible = state.showPikachuRunning,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.pikachu_running)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Pikachu Running",
                    modifier = Modifier.size(40.dp)
                )
            }
            
            // Timer de entrenamiento (no pausable)
            Text(
                text = state.timerValue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE8E3FF),
                textAlign = TextAlign.Center
            )
            
            // Estado del entrenamiento
            Text(
                text = stringResource(R.string.training_active),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun HeaderSection(
    state: StrengthTrainingState,
    onAction: (StrengthTrainingAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            if (!state.isTrainingStarted) {
                // Sección de selección de ejercicios antes del entrenamiento
                Text(
                    text = stringResource(R.string.what_to_train),
                    fontSize = 20.sp,
                    color = Color(0xFFE8E3FF),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Dropdown para seleccionar ejercicio
                ExerciseDropdown(
                    selectedExercise = state.selectedExercise,
                    availableExercises = state.availableExercises,
                    onExerciseSelected = { onAction(StrengthTrainingAction.SelectExercise(it)) },
                    enabled = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Botón para agregar ejercicio
                Button(
                    onClick = { onAction(StrengthTrainingAction.AddExerciseToWorkout) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.add_exercise),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Lista de ejercicios seleccionados
                if (state.selectedExercises.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.exercises_for_training),
                        fontSize = 16.sp,
                        color = Color(0xFFE8E3FF),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            state.selectedExercises.forEach { exercise ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.exercise_format, exercise),
                                        fontSize = 14.sp,
                                        color = Color(0xFFE8E3FF),
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    Button(
                                        onClick = { onAction(StrengthTrainingAction.RemoveExerciseFromWorkout(exercise)) },
                                        modifier = Modifier.size(32.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFF6B6B)
                                        ),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(
                                            text = "×",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                }
                
                // Botón iniciar entrenamiento
                Button(
                    onClick = { onAction(StrengthTrainingAction.StartTraining) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6)
                    ),
                    enabled = state.selectedExercises.isNotEmpty()
                ) {
                    Text(
                        text = stringResource(R.string.start_training_button),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Sección durante el entrenamiento - Selector de ejercicios actuales
                Text(
                    text = stringResource(R.string.exercises_for_training),
                    fontSize = 16.sp,
                    color = Color(0xFFE8E3FF),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Scroll horizontal de ejercicios
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(state.selectedExercises) { index, exercise ->
                        Card(
                            modifier = Modifier
                                .clickable { onAction(StrengthTrainingAction.ChangeCurrentExercise(index)) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (index == state.currentExerciseIndex) 
                                    Color(0xFF8B5CF6) 
                                else 
                                    Color.Black.copy(alpha = 0.3f)
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (index == state.currentExerciseIndex) 6.dp else 2.dp
                            )
                        ) {
                            Text(
                                text = exercise,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = if (index == state.currentExerciseIndex) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                
                if (state.completedExercises.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.completed_format, state.completedExercises.joinToString(", ")),
                        fontSize = 12.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactRestTimer(
    state: StrengthTrainingState,
    onAction: (StrengthTrainingAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = StreakColor.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Timer info
            Column {
                Text(
                    text = stringResource(R.string.rest_timer),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = state.restTimeValue,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = StreakColor
                )
            }
            
            // Timer Controls (compactos)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Subtract Time Button
                Button(
                    onClick = { onAction(StrengthTrainingAction.SubtractRestTime) },
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "-30",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Skip Button
                Button(
                    onClick = { onAction(StrengthTrainingAction.SkipRestTimer) },
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "×",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Add Time Button
                Button(
                    onClick = { onAction(StrengthTrainingAction.AddRestTime) },
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4ECDC4)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "+30",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RestTimerMainSection(
    state: StrengthTrainingState,
    onAction: (StrengthTrainingAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = StreakColor.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Timer Title
            Text(
                text = stringResource(R.string.rest_timer),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Timer Display (más pequeño)
            Text(
                text = state.restTimeValue,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = StreakColor,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = stringResource(R.string.rest_time_remaining),
                fontSize = 16.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Timer Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Subtract Time Button
                Button(
                    onClick = { onAction(StrengthTrainingAction.SubtractRestTime) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.subtract_time),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Skip Button
                Button(
                    onClick = { onAction(StrengthTrainingAction.SkipRestTimer) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.skip_rest),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Add Time Button
                Button(
                    onClick = { onAction(StrengthTrainingAction.AddRestTime) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4ECDC4)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.add_time),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseDropdown(
    selectedExercise: String,
    availableExercises: List<String>,
    onExerciseSelected: (String) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded && enabled }
    ) {
        OutlinedTextField(
            value = selectedExercise,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Black.copy(alpha = 0.2f),
                unfocusedContainerColor = Color.Black.copy(alpha = 0.2f),
                disabledContainerColor = Color.Black.copy(alpha = 0.1f),
                focusedTextColor = Color(0xFFE8E3FF),
                unfocusedTextColor = Color(0xFFE8E3FF),
                disabledTextColor = Color(0xFFE8E3FF).copy(alpha = 0.6f),
                focusedBorderColor = Color(0xFF8B5CF6),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1A1A2E))
        ) {
            availableExercises.forEach { exercise ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = exercise,
                            color = Color(0xFFE8E3FF)
                        )
                    },
                    onClick = {
                        onExerciseSelected(exercise)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Color(0xFFE8E3FF)
                    )
                )
            }
        }
    }
}

@Composable
private fun ExerciseTableSection(
    state: StrengthTrainingState,
    onAction: (StrengthTrainingAction) -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
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
                // Table Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.selectedExercises.isNotEmpty()) 
                            state.selectedExercises[state.currentExerciseIndex] 
                        else 
                            state.selectedExercise,
                        fontSize = 18.sp,
                        color = Color(0xFFE8E3FF),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Add Set Button
                    Button(
                        onClick = { onAction(StrengthTrainingAction.AddSet) },
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B5CF6)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.plus_symbol),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Table Headers
                TableHeaderRow()
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Exercise Rows
                state.exerciseRows.forEachIndexed { index, row ->
                    ExerciseRowItem(
                        row = row,
                        onToggleComplete = { onAction(StrengthTrainingAction.ToggleSetComplete(index)) },
                        onWeightChange = { weight -> onAction(StrengthTrainingAction.UpdateWeight(index, weight)) },
                        onRepsChange = { reps -> onAction(StrengthTrainingAction.UpdateReps(index, reps)) }
                    )
                    if (index < state.exerciseRows.size - 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeaderRow() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HeaderCell(stringResource(R.string.prev_header), Modifier.weight(1f))
            HeaderCell(stringResource(R.string.set_header), Modifier.weight(1f))
            HeaderCell(stringResource(R.string.kg_header), Modifier.weight(1f))
            HeaderCell(stringResource(R.string.reps_header), Modifier.weight(1f))
            HeaderCell(stringResource(R.string.done_header), Modifier.weight(1f))
        }
    }
}

@Composable
private fun HeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color(0xFFE8E3FF),
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
private fun ExerciseRowItem(
    row: ExerciseRow,
    onToggleComplete: () -> Unit,
    onWeightChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous column
            RowCell(row.previous, Modifier.weight(1f))
            
            // Set number column
            RowCell(row.set.toString(), Modifier.weight(1f))
            
            // Weight column - Editable
            EditableCell(
                value = row.weight.toString(),
                onValueChange = { newValue ->
                    val weight = newValue.toIntOrNull()
                    if (weight != null && weight >= 0 && weight <= 999) {
                        onWeightChange(weight)
                    }
                },
                modifier = Modifier.weight(1f)
            )
            
            // Reps column - Editable
            EditableCell(
                value = row.reps.toString(),
                onValueChange = { newValue ->
                    val reps = newValue.toIntOrNull()
                    if (reps != null && reps >= 0 && reps <= 99) {
                        onRepsChange(reps)
                    }
                },
                modifier = Modifier.weight(1f)
            )
            
            // Complete Button
            Text(
                text = if (row.isCompleted) stringResource(R.string.yes) else stringResource(R.string.no),
                color = if (row.isCompleted) Color(0xFF4CAF50) else Color(0xFFFF5722),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onToggleComplete() }
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun EditableCell(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var textValue by remember(value) { mutableStateOf(value) }
    
    if (isEditing) {
        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue: String ->
                // Only allow numbers
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*$"))) {
                    textValue = newValue
                }
            },
            singleLine = true,
            modifier = modifier
                .height(48.dp)
                .padding(2.dp),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color(0xFFE8E3FF)
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2A2A3E),
                unfocusedContainerColor = Color(0xFF2A2A3E),
                focusedTextColor = Color(0xFFE8E3FF),
                unfocusedTextColor = Color(0xFFE8E3FF),
                focusedBorderColor = StreakColor,
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(8.dp),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (textValue.isNotEmpty()) {
                        onValueChange(textValue)
                    }
                    isEditing = false
                }
            )
        )
    } else {
        Box(
            modifier = modifier
                .height(48.dp)
                .clickable { 
                    isEditing = true
                    textValue = value
                }
                .background(
                    color = Color(0xFF1A1A2E),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                color = Color(0xFFE8E3FF),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun RowCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color(0xFFE8E3FF),
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
private fun FloatingRestTimer(
    state: StrengthTrainingState,
    onAction: (StrengthTrainingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    // Mostrar cuando el temporizador está activo pero no es el foco principal
    AnimatedVisibility(
        visible = state.isRestTimerActive,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .width(120.dp)
                .height(80.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = StreakColor.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = state.restTimeValue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Descanso",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Mini botón skip
                    Button(
                        onClick = { onAction(StrengthTrainingAction.SkipRestTimer) },
                        modifier = Modifier
                            .size(20.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B6B)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "×",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Mini botón +30s
                    Button(
                        onClick = { onAction(StrengthTrainingAction.AddRestTime) },
                        modifier = Modifier
                            .size(20.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4ECDC4)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "+",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutHistorySection(
    state: StrengthTrainingState,
    onAction: (StrengthTrainingAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.workout_history),
                    fontSize = 20.sp,
                    color = Color(0xFFE8E3FF),
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = { onAction(StrengthTrainingAction.ToggleWorkoutHistory) },
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6).copy(alpha = 0.8f)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(
                            if (state.showWorkoutHistory) R.string.hide_history
                            else R.string.view_history
                        ),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            if (state.showWorkoutHistory) {
                Spacer(modifier = Modifier.height(16.dp))
                
                when {
                    state.isLoadingHistory -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = Color(0xFF8B5CF6)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.loading_history),
                                    fontSize = 12.sp,
                                    color = Color(0xFFE8E3FF).copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                    
                    state.workoutHistory.isEmpty() -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🏋️‍♂️",
                                    fontSize = 32.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = stringResource(R.string.no_workouts_yet),
                                    fontSize = 14.sp,
                                    color = Color(0xFFE8E3FF),
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.start_first_workout),
                                    fontSize = 12.sp,
                                    color = Color(0xFFE8E3FF).copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    else -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(state.workoutHistory.take(5).size) { index ->
                                val workout = state.workoutHistory[index]
                                WorkoutHistoryCard(workout = workout)
                            }
                        }
                        
                        if (state.workoutHistory.size > 5) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Y ${state.workoutHistory.size - 5} entrenamientos más...",
                                fontSize = 11.sp,
                                color = Color(0xFFE8E3FF).copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutHistoryCard(
    workout: WorkoutSession
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8B5CF6).copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = workout.date,
                    fontSize = 12.sp,
                    color = Color(0xFF8B5CF6),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.workout_duration_format,
                        workout.totalDurationFormatted
                    ),
                    fontSize = 10.sp,
                    color = Color(0xFFE8E3FF).copy(alpha = 0.8f)
                )
            }
            
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.workout_exercises_count, workout.exercises.size),
                        fontSize = 10.sp,
                        color = Color(0xFFE8E3FF).copy(alpha = 0.7f)
                    )
                    Text(
                        text = "💪",
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(
                        R.string.workout_sets_count,
                        workout.exercises.sumOf { it.sets.size }
                    ),
                    fontSize = 10.sp,
                    color = Color(0xFFE8E3FF).copy(alpha = 0.7f)
                )
            }
        }
    }
}

private fun formatDuration(minutes: Int): String {
    return if (minutes < 60) {
        "${minutes}min"
    } else {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        "${hours}h ${remainingMinutes}min"
    }
}

// Data classes and helper functions are now in StrengthTrainingState.kt

@Preview(showBackground = true)
@Composable
fun StrengthTrainingScreenPreview() {
    PokeFitComposeTheme {
        StrengthTrainingScreen()
    }
}
