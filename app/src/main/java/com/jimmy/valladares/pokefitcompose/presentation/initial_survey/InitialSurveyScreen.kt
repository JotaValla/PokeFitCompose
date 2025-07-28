package com.jimmy.valladares.pokefitcompose.presentation.initial_survey

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.jimmy.valladares.pokefitcompose.presentation.initial_survey.providers.InitialSurveyPreviewProvider
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientEnd
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientStart
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitComposeTheme

@Composable
fun InitialSurveyScreenRoot(
    onNavigateToNextStep: () -> Unit,
    onNavigateToTrainingSurvey: () -> Unit,
    onNavigateToStepsSurvey: () -> Unit,
    onNavigateBack: () -> Unit
) {
    InitialSurveyScreen(
        onNavigateToNextStep = onNavigateToNextStep,
        onNavigateToTrainingSurvey = onNavigateToTrainingSurvey,
        onNavigateToStepsSurvey = onNavigateToStepsSurvey,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun InitialSurveyScreen(
    onNavigateToNextStep: () -> Unit,
    onNavigateToTrainingSurvey: () -> Unit,
    onNavigateToStepsSurvey: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: InitialSurveyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is InitialSurveyEvent.NavigateToNextStep -> onNavigateToNextStep()
                is InitialSurveyEvent.NavigateToTrainingSurvey -> onNavigateToTrainingSurvey()
                is InitialSurveyEvent.NavigateToStepsSurvey -> onNavigateToStepsSurvey()
                is InitialSurveyEvent.NavigateBack -> onNavigateBack()
                is InitialSurveyEvent.ShowError -> {
                    // Aquí podrías mostrar un Snackbar o AlertDialog
                }
            }
        }
    }
    
    InitialSurveyContent(
        state = state,
        onAction = viewModel::onAction
    )
    
    // Mostrar errores si existen
    state.error?.let { error ->
        LaunchedEffect(error) {
            // Aquí podrías mostrar un Snackbar
            viewModel.onAction(InitialSurveyAction.DismissError)
        }
    }
}

@Composable
private fun InitialSurveyContent(
    state: InitialSurveyState,
    onAction: (InitialSurveyAction) -> Unit
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                
                // Título principal
                Text(
                    text = "Somos PokeFit",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
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
                
                // Opciones de objetivos
                FitnessGoalOption(
                    goal = FitnessGoal.EXIT_SEDENTARY,
                    isSelected = state.selectedGoal == FitnessGoal.EXIT_SEDENTARY,
                    isEnabled = !state.isLoading,
                    onClick = { onAction(InitialSurveyAction.SelectGoal(FitnessGoal.EXIT_SEDENTARY)) }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                FitnessGoalOption(
                    goal = FitnessGoal.IMPROVE_TRAINING,
                    isSelected = state.selectedGoal == FitnessGoal.IMPROVE_TRAINING,
                    isEnabled = !state.isLoading,
                    onClick = { onAction(InitialSurveyAction.SelectGoal(FitnessGoal.IMPROVE_TRAINING)) }
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Botón para continuar (solo aparece cuando hay una selección)
                if (state.canProceed) {
                    Button(
                        onClick = { onAction(InitialSurveyAction.ProceedToNext) },
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

@Composable
private fun FitnessGoalOption(
    goal: FitnessGoal,
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
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = if (isSelected) {
                    "${goal.displayName} - Seleccionado"
                } else {
                    "${goal.displayName} - No seleccionado"
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
            Text(
                text = goal.displayName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                ),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )
            
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
fun InitialSurveyScreenPreview(
    @PreviewParameter(InitialSurveyPreviewProvider::class) state: InitialSurveyState
) {
    PokeFitComposeTheme {
        InitialSurveyContent(
            state = state,
            onAction = {}
        )
    }
}
