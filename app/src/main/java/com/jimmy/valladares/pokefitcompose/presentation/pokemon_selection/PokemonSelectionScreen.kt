package com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import com.jimmy.valladares.pokefitcompose.R
import com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection.providers.PokemonSelectionPreviewProvider
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientEnd
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientStart
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitComposeTheme

@Composable
fun PokemonSelectionScreenRoot(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PokemonSelectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is PokemonSelectionEvent.NavigateToHome -> onNavigateToHome()
                is PokemonSelectionEvent.NavigateBack -> onNavigateBack()
                is PokemonSelectionEvent.ShowError -> {
                    // Se puede implementar un Snackbar aquí si es necesario
                }
                is PokemonSelectionEvent.ShowConfirmation -> {
                    // Se puede implementar un diálogo de confirmación aquí si es necesario
                }
            }
        }
    }
    
    PokemonSelectionScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun PokemonSelectionScreen(
    state: PokemonSelectionState,
    onAction: (PokemonSelectionAction) -> Unit
) {
    PokemonSelectionContent(
        state = state,
        onAction = onAction
    )
    
    // Mostrar errores si existen
    state.error?.let { error ->
        LaunchedEffect(error) {
            // Solo dismiss automáticamente errores generales
            onAction(PokemonSelectionAction.DismissError)
        }
    }
}

@Composable
private fun PokemonSelectionContent(
    state: PokemonSelectionState,
    onAction: (PokemonSelectionAction) -> Unit
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
                        onClick = { onAction(PokemonSelectionAction.Back) },
                        modifier = Modifier.semantics {
                            contentDescription = "Regresar a encuesta anterior"
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
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Título de selección
                    Text(
                        text = "Elige tu Pokémon",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Opciones de Pokémon
                    PokemonOption(
                        pokemon = Pokemon.TOTODILE,
                        isSelected = state.selectedPokemon == Pokemon.TOTODILE,
                        isEnabled = !state.isLoading,
                        onClick = { onAction(PokemonSelectionAction.SelectPokemon(Pokemon.TOTODILE)) }
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    PokemonOption(
                        pokemon = Pokemon.EEVEE,
                        isSelected = state.selectedPokemon == Pokemon.EEVEE,
                        isEnabled = !state.isLoading,
                        onClick = { onAction(PokemonSelectionAction.SelectPokemon(Pokemon.EEVEE)) }
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    PokemonOption(
                        pokemon = Pokemon.PIPLUP,
                        isSelected = state.selectedPokemon == Pokemon.PIPLUP,
                        isEnabled = !state.isLoading,
                        onClick = { onAction(PokemonSelectionAction.SelectPokemon(Pokemon.PIPLUP)) }
                    )
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    // Mensaje de confirmación cuando se selecciona un Pokémon
                    AnimatedVisibility(
                        visible = state.selectedPokemon != null && !state.isLoading,
                        enter = slideInVertically(
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        ) + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "¡Has elegido a ${state.selectedPokemon?.displayName ?: ""}!",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Preparando tu aventura fitness...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Loading indicator si está cargando
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Guardando tu selección...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    
                    // Texto disclaimer en la parte inferior
                    Text(
                        text = "Recuerda que una vez elegido tu pokémon no podrás cambiarlo.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = Color.Gray.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    )
                }
            }
            
            // Mostrar error si existe
            AnimatedVisibility(
                visible = state.error != null,
                enter = slideInVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn(),
                exit = slideOutVertically() + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                state.error?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonOption(
    pokemon: Pokemon,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pokemon_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isEnabled) 1.0f else 0.5f,
        animationSpec = tween(durationMillis = 200),
        label = "pokemon_alpha"
    )
    
    Column(
        modifier = Modifier
            .scale(scale)
            .clickable(
                enabled = isEnabled,
                indication = ripple(bounded = false, radius = 80.dp),
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .semantics {
                contentDescription = if (isSelected) {
                    "${pokemon.displayName} - Seleccionado. ${pokemon.description}"
                } else {
                    "${pokemon.displayName} - No seleccionado. ${pokemon.description}"
                }
            }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // GIF del Pokémon usando Coil AsyncImage para soporte completo de GIFs
        AsyncImage(
            model = pokemon.drawableRes,
            contentDescription = pokemon.description,
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer(alpha = alpha),
            contentScale = ContentScale.Fit,
            placeholder = painterResource(id = R.drawable.pokeball),
            error = painterResource(id = R.drawable.pokeball)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Nombre del Pokémon
        Text(
            text = pokemon.displayName,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 18.sp
            ),
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onPrimary
            }.copy(alpha = alpha),
            textAlign = TextAlign.Center
        )
        
        // Indicador de selección
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}

// Composable personalizado para manejar GIFs (alternativa optimizada)
@Composable
private fun AnimatedGifImage(
    drawableRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    // Para esta implementación básica, usamos Image normal
    // En una implementación completa, podrías usar una librería como Coil para GIFs
    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = contentDescription,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun PokemonSelectionScreenPreview(
    @PreviewParameter(PokemonSelectionPreviewProvider::class) state: PokemonSelectionState
) {
    PokeFitComposeTheme {
        PokemonSelectionContent(
            state = state,
            onAction = {}
        )
    }
}
