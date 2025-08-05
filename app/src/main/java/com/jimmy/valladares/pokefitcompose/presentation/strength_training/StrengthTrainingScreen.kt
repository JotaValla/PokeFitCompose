package com.jimmy.valladares.pokefitcompose.presentation.strength_training

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.jimmy.valladares.pokefitcompose.R
import com.jimmy.valladares.pokefitcompose.presentation.home.BottomNavItem
import com.jimmy.valladares.pokefitcompose.presentation.navigation.BottomNavigationBar
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientEnd
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientStart
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitComposeTheme

@Composable
fun StrengthTrainingScreen(
    onNavigateToTab: (String) -> Unit = {},
    viewModel: StrengthTrainingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is StrengthTrainingEvent.TrainingCompleted -> {
                    // Handle training completion
                }
                is StrengthTrainingEvent.ShowMessage -> {
                    // Show snackbar or toast
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F23))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), // Espacio para bottom navigation
            contentPadding = PaddingValues(
                top = 60.dp,
                start = 24.dp,
                end = 24.dp,
                bottom = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Section con Timer y Pikachu
            item {
                HeaderSection(
                    state = state,
                    onAction = viewModel::onAction
                )
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
                                text = "Ejercicios Completados",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            state.completedExercises.forEach { exercise ->
                                val sets = state.exerciseHistory[exercise]?.count { it.isCompleted } ?: 0
                                Text(
                                    text = "• $exercise ($sets series completadas)",
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
                            text = "Finalizar Entrenamiento",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Bottom Navigation
        BottomNavigationBar(
            selectedTab = BottomNavItem.TRAINING,
            onTabSelected = { tab -> onNavigateToTab(tab.route) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
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
            // Timer Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pikachu Running GIF
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
                        modifier = Modifier.size(80.dp)
                    )
                }
                
                // Timer Display
                Text(
                    text = state.timerValue,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE8E3FF),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                
                // Pause/Resume Button (only visible when training started)
                if (state.isTrainingStarted) {
                    Button(
                        onClick = { onAction(StrengthTrainingAction.PauseResumeTimer) },
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isPaused) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = if (state.isPaused) "▶️" else "⏸️",
                            fontSize = 24.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Training Status Section (only visible when training started)
            if (state.isTrainingStarted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.isPaused) 
                            Color(0xFFFF9800).copy(alpha = 0.2f) 
                        else 
                            Color(0xFF4CAF50).copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = if (state.isPaused) "PAUSADO" else "ENTRENANDO",
                        color = if (state.isPaused) Color(0xFFFF9800) else Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Training Question (only visible when training not started)
            if (!state.isTrainingStarted) {
                Text(
                    text = "¿Qué deseas entrenar ahora?",
                    fontSize = 20.sp,
                    color = Color(0xFFE8E3FF),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Dropdown Spinner (using exposed dropdown menu)
                ExerciseDropdown(
                    selectedExercise = state.selectedExercise,
                    availableExercises = state.availableExercises,
                    onExerciseSelected = { onAction(StrengthTrainingAction.SelectExercise(it)) },
                    enabled = !state.isTrainingStarted
                )
                
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                // Show exercise selector when training is started
                Column {
                    Text(
                        text = "Ejercicio Actual:",
                        fontSize = 16.sp,
                        color = Color(0xFFE8E3FF),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Dropdown to change exercise during training
                    ExerciseDropdown(
                        selectedExercise = state.selectedExercise,
                        availableExercises = state.availableExercises,
                        onExerciseSelected = { onAction(StrengthTrainingAction.SelectExercise(it)) },
                        enabled = true // Ahora siempre habilitado durante el entrenamiento
                    )
                    
                    // Show completed exercises indicator
                    if (state.completedExercises.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Completados: ${state.completedExercises.joinToString(", ")}",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            // Start Training Button
            Button(
                onClick = { onAction(StrengthTrainingAction.StartTraining) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5CF6)
                ),
                enabled = !state.isTrainingStarted
            ) {
                Text(
                    text = "Iniciar Entrenamiento",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
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
                        text = state.selectedExercise,
                        fontSize = 18.sp,
                        color = Color(0xFFE8E3FF),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Switch Exercise Button
                    Button(
                        onClick = { onAction(StrengthTrainingAction.SwitchExercise) },
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "↻",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
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
                            text = "+",
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
            HeaderCell("Prev", Modifier.weight(1f))
            HeaderCell("Serie", Modifier.weight(1f))
            HeaderCell("KG", Modifier.weight(1f))
            HeaderCell("Reps", Modifier.weight(1f))
            HeaderCell("Hecho", Modifier.weight(1f))
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
                text = if (row.isCompleted) "Sí" else "No",
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
            onValueChange = { newValue ->
                // Only allow numbers
                if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                    textValue = newValue
                }
            },
            singleLine = true,
            modifier = modifier
                .height(40.dp),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color(0xFFE8E3FF)
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color(0xFFE8E3FF),
                unfocusedTextColor = Color(0xFFE8E3FF),
                focusedBorderColor = Color(0xFF8B5CF6),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
        Text(
            text = value,
            color = Color(0xFFE8E3FF),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = modifier
                .clickable { 
                    isEditing = true
                    textValue = value
                }
                .padding(vertical = 8.dp)
        )
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

// Data classes and helper functions are now in StrengthTrainingState.kt

@Preview(showBackground = true)
@Composable
fun StrengthTrainingScreenPreview() {
    PokeFitComposeTheme {
        StrengthTrainingScreen()
    }
}
