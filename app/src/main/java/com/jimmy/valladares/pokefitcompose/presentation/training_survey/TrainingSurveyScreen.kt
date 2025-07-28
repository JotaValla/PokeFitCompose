package com.jimmy.valladares.pokefitcompose.presentation.training_survey

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jimmy.valladares.pokefitcompose.R
import com.jimmy.valladares.pokefitcompose.presentation.training_survey.providers.TrainingSurveyPreviewProvider
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientEnd
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientStart
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitComposeTheme

@Composable
fun TrainingSurveyScreenRoot(
    onNavigateToNextStep: () -> Unit,
    onNavigateBack: () -> Unit
) {
    TrainingSurveyScreen(
        onNavigateToNextStep = onNavigateToNextStep,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun TrainingSurveyScreen(
    onNavigateToNextStep: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TrainingSurveyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is TrainingSurveyEvent.NavigateToNextStep -> onNavigateToNextStep()
                is TrainingSurveyEvent.NavigateBack -> onNavigateBack()
                is TrainingSurveyEvent.ShowError -> {
                    // Aquí podrías mostrar un Snackbar o AlertDialog
                }
            }
        }
    }
    
    TrainingSurveyContent(
        state = state,
        onAction = viewModel::onAction
    )
    
    // Mostrar errores si existen
    state.error?.let { error ->
        LaunchedEffect(error) {
            // Aquí podrías mostrar un Snackbar
            viewModel.onAction(TrainingSurveyAction.DismissError)
        }
    }
}

@Composable
private fun TrainingSurveyContent(
    state: TrainingSurveyState,
    onAction: (TrainingSurveyAction) -> Unit
) {
    PokeFitComposeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GradientStart.copy(alpha = 0.9f),
                            GradientEnd.copy(alpha = 0.9f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Header con botón de regresar y título
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onAction(TrainingSurveyAction.Back) },
                        modifier = Modifier.semantics {
                            contentDescription = "Regresar a encuesta inicial"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    Text(
                        text = "Somos PokeFit",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .offset(x = (-14).dp) // Compensar el espacio del IconButton para centrar
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Contenido centrado
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo Pokeball
                    Image(
                        painter = painterResource(id = R.drawable.pokeball),
                        contentDescription = "PokeBall",
                        modifier = Modifier
                            .size(100.dp)
                            .semantics {
                                contentDescription = "Logo de PokeFit"
                            }
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Pregunta principal
                    Text(
                        text = "¿Cuál es tu objetivo?",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    // Opciones de entrenamiento
                    TrainingGoalOption(
                        goal = TrainingGoal.VELOCITY,
                        isSelected = state.selectedTrainingGoal == TrainingGoal.VELOCITY,
                        isEnabled = !state.isLoading,
                        onClick = { onAction(TrainingSurveyAction.SelectTrainingGoal(TrainingGoal.VELOCITY)) }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    TrainingGoalOption(
                        goal = TrainingGoal.STRENGTH,
                        isSelected = state.selectedTrainingGoal == TrainingGoal.STRENGTH,
                        isEnabled = !state.isLoading,
                        onClick = { onAction(TrainingSurveyAction.SelectTrainingGoal(TrainingGoal.STRENGTH)) }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    TrainingGoalOption(
                        goal = TrainingGoal.RESISTANCE,
                        isSelected = state.selectedTrainingGoal == TrainingGoal.RESISTANCE,
                        isEnabled = !state.isLoading,
                        onClick = { onAction(TrainingSurveyAction.SelectTrainingGoal(TrainingGoal.RESISTANCE)) }
                    )
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    // Botón para continuar (solo aparece cuando hay una selección)
                    if (state.canProceed) {
                        Button(
                            onClick = { onAction(TrainingSurveyAction.ProceedToNext) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !state.isLoading,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    text = "Continuar",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun TrainingGoalOption(
    goal: TrainingGoal,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            Color.White
        },
        animationSpec = tween(durationMillis = 200),
        label = "background_color"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 200),
        label = "border_color"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 200),
        label = "text_color"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = if (isSelected) {
                    "${goal.displayName} - Seleccionado. ${goal.description}"
                } else {
                    "${goal.displayName} - No seleccionado. ${goal.description}"
                }
            },
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, borderColor)
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = goal.displayName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    ),
                    color = textColor
                )
                
                if (isSelected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = goal.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 14.sp
                        ),
                        color = textColor.copy(alpha = 0.8f)
                    )
                }
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Seleccionar ${goal.displayName}",
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color(0xFF4CAF50) // Verde
                },
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrainingSurveyScreenPreview(
    @PreviewParameter(TrainingSurveyPreviewProvider::class) state: TrainingSurveyState
) {
    PokeFitComposeTheme {
        TrainingSurveyContent(
            state = state,
            onAction = {}
        )
    }
}
