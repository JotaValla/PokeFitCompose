package com.jimmy.valladares.pokefitcompose.presentation.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientEnd
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientStart
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitComposeTheme
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitGray
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitGreen
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitWhite

@Composable
fun WelcomeScreenRoot(
    navigateToLogin: () -> Unit,
    viewModel: WelcomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(key1 = true) {
        viewModel.events.collect { event ->
            when (event) {
                WelcomeScreenEvent.NavigateToLogin -> {
                    navigateToLogin()
                }
            }
        }
    }
    
    WelcomeScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun WelcomeScreen(
    state: WelcomeScreenState,
    onAction: (WelcomeScreenAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GradientStart,
                        GradientEnd
                    )
                )
            )
            .semantics {
                contentDescription = "Welcome screen with PokeFit branding"
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(120.dp))
            
            // Título PokeFit
            Text(
                text = "PokeFit",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = PokeFitWhite,
                modifier = Modifier.semantics {
                    contentDescription = "PokeFit app title"
                }
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Pokeball icon
            Image(
                painter = painterResource(id = R.drawable.pokeball),
                contentDescription = "Pokeball icon",
                modifier = Modifier
                    .size(120.dp)
                    .semantics {
                        contentDescription = "Pokeball icon representing Pokemon theme"
                    }
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Descripción
            Text(
                text = "¿Listo para ponerte en forma mientras entrenas como un verdadero Maestro Pokémon?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    lineHeight = 26.sp
                ),
                color = PokeFitGray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "App description about fitness training like a Pokemon master"
                    }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Botón Get Started
            FloatingActionButton(
                onClick = {
                    onAction(WelcomeScreenAction.OnGetStarted)
                },
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .size(72.dp)
                    .semantics {
                        contentDescription = "Get started button to begin using the app"
                    },
                shape = CircleShape,
                containerColor = PokeFitWhite,
                contentColor = PokeFitGreen
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Arrow forward",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(
    name = "Welcome Screen Light",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun WelcomeScreenPreview(
    @PreviewParameter(WelcomeScreenStatePreviewProvider::class) state: WelcomeScreenState
) {
    PokeFitComposeTheme(darkTheme = false) {
        WelcomeScreen(
            state = state,
            onAction = {}
        )
    }
}

@Preview(
    name = "Welcome Screen Dark",
    showBackground = true,
    widthDp = 360,
    heightDp = 640,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun WelcomeScreenDarkPreview() {
    PokeFitComposeTheme(darkTheme = true) {
        WelcomeScreen(
            state = WelcomeScreenState(),
            onAction = {}
        )
    }
}
