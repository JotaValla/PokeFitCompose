package com.jimmy.valladares.pokefitcompose.presentation.steps_survey

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jimmy.valladares.pokefitcompose.R
import com.jimmy.valladares.pokefitcompose.presentation.steps_survey.providers.StepsSurveyPreviewProvider
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientEnd
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientStart
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitComposeTheme

@Composable
fun StepsSurveyScreenRoot(
    onNavigateToNextStep: () -> Unit,
    onNavigateBack: () -> Unit
) {
    StepsSurveyScreen(
        onNavigateToNextStep = onNavigateToNextStep,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun StepsSurveyScreen(
    onNavigateToNextStep: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: StepsSurveyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is StepsSurveyEvent.NavigateToNextStep -> onNavigateToNextStep()
                is StepsSurveyEvent.NavigateBack -> onNavigateBack()
                is StepsSurveyEvent.ShowValidationError -> {
                    // Aquí podrías mostrar un Snackbar específico para errores de validación
                }
                is StepsSurveyEvent.ShowError -> {
                    // Aquí podrías mostrar un Snackbar para errores generales
                }
                is StepsSurveyEvent.ShowSuccessFeedback -> {
                    // Aquí podrías mostrar una animación de éxito
                }
            }
        }
    }
    
    StepsSurveyContent(
        state = state,
        onAction = viewModel::onAction
    )
    
    // Mostrar errores si existen
    state.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Solo dismiss automáticamente errores generales, no de validación
            if (!error.contains("válido") && !error.contains("entre")) {
                viewModel.onAction(StepsSurveyAction.DismissError)
            }
        }
    }
}

@Composable
private fun StepsSurveyContent(
    state: StepsSurveyState,
    onAction: (StepsSurveyAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    
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
                        onClick = { onAction(StepsSurveyAction.Back) },
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
                        text = "¿Cuántos pasos al día realizas?",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    // Campo de entrada de pasos
                    OutlinedTextField(
                        value = state.stepsInputText,
                        onValueChange = { onAction(StepsSurveyAction.UpdateStepsInput(it)) },
                        label = null,
                        placeholder = { 
                            Text(
                                "Número de Pasos",
                                color = Color.Gray.copy(alpha = 0.7f)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onAction(StepsSurveyAction.ConfirmSteps)
                            }
                        ),
                        isError = state.errorMessage != null,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .semantics {
                                contentDescription = "Campo para ingresar número de pasos diarios, entre 1000 y 50000"
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = if (state.isValidInput) {
                                Color(0xFF4CAF50) // Verde para válido
                            } else if (state.errorMessage != null) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            unfocusedBorderColor = if (state.isValidInput) {
                                Color(0xFF4CAF50).copy(alpha = 0.7f)
                            } else if (state.errorMessage != null) {
                                MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                            } else {
                                Color.White.copy(alpha = 0.5f)
                            },
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                        )
                    )
                    
                    // Mensaje de error con animación
                    AnimatedVisibility(
                        visible = state.errorMessage != null,
                        enter = slideInVertically(
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        ) + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        state.errorMessage?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Botón OK con animación
                    val buttonColor by animateColorAsState(
                        targetValue = if (state.isValidInput) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Gray
                        },
                        animationSpec = tween(durationMillis = 300),
                        label = "button_color"
                    )
                    
                    Button(
                        onClick = { onAction(StepsSurveyAction.ConfirmSteps) },
                        modifier = Modifier
                            .width(120.dp)
                            .height(50.dp),
                        enabled = state.isValidInput && !state.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isValidInput) {
                                Color.White
                            } else {
                                Color.White.copy(alpha = 0.5f)
                            },
                            disabledContainerColor = Color.White.copy(alpha = 0.3f)
                        )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "OK",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = if (state.isValidInput) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Gray
                                }
                            )
                        }
                    }
                    
                    // Texto de ayuda
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Ingresa un número entre ${StepsSurveyState.MIN_STEPS} y ${StepsSurveyState.MAX_STEPS} pasos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
    
    // Auto-focus en el campo de texto cuando se carga la pantalla
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview(showBackground = true)
@Composable
fun StepsSurveyScreenPreview(
    @PreviewParameter(StepsSurveyPreviewProvider::class) state: StepsSurveyState
) {
    PokeFitComposeTheme {
        StepsSurveyContent(
            state = state,
            onAction = {}
        )
    }
}
