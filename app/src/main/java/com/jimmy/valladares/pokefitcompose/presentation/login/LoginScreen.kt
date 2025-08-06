package com.jimmy.valladares.pokefitcompose.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jimmy.valladares.pokefitcompose.R
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientEnd
import com.jimmy.valladares.pokefitcompose.ui.theme.GradientStart
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitComposeTheme
import com.jimmy.valladares.pokefitcompose.ui.theme.PokeFitGray

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginScreenEvent.NavigateToHome -> onNavigateToHome()
                is LoginScreenEvent.NavigateToRegister -> onNavigateToRegister()
                is LoginScreenEvent.NavigateToForgotPassword -> onNavigateToForgotPassword()
                is LoginScreenEvent.ShowMessage -> showMessage = event.message
            }
        }
    }
    
    PokeFitComposeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GradientStart.copy(alpha = 0.8f),
                            GradientEnd.copy(alpha = 0.8f)
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
                
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.pokeball),
                    contentDescription = "PokeBall",
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Título
                Text(
                    text = "¡Bienvenido de nuevo!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    color = PokeFitGray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Inicia sesión para continuar tu aventura",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PokeFitGray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Formulario de login
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Campo de email
                        OutlinedTextField(
                            value = state.email,
                            onValueChange = { viewModel.onAction(LoginScreenAction.OnEmailChange(it)) },
                            label = { Text("Email") },
                            placeholder = { Text("Ingresa tu email") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            isError = state.emailError != null,
                            supportingText = state.emailError?.let { { Text(it) } },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Campo de contraseña
                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { viewModel.onAction(LoginScreenAction.OnPasswordChange(it)) },
                            label = { Text("Contraseña") },
                            placeholder = { Text("Ingresa tu contraseña") },
                            visualTransformation = if (state.isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        viewModel.onAction(LoginScreenAction.OnTogglePasswordVisibility)
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (state.isPasswordVisible) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },
                                        contentDescription = if (state.isPasswordVisible) {
                                            "Ocultar contraseña"
                                        } else {
                                            "Mostrar contraseña"
                                        }
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            isError = state.passwordError != null,
                            supportingText = state.passwordError?.let { { Text(it) } },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Enlace "¿Olvidaste tu contraseña?"
                        TextButton(
                            onClick = {
                                viewModel.onAction(LoginScreenAction.OnForgotPasswordClick)
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Botón de inicio de sesión
                        Button(
                            onClick = {
                                viewModel.onAction(LoginScreenAction.OnSignInClick)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !state.isLoading,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    text = "Iniciar Sesión",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Enlace para registrarse
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "¿No tienes cuenta? ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(
                                onClick = {
                                    viewModel.onAction(LoginScreenAction.OnRegisterClick)
                                }
                            ) {
                                Text(
                                    text = "Regístrate",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
    
    // Mostrar mensaje de éxito
    showMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(3000) // Mostrar por 3 segundos
            showMessage = null
        }
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    
    // Mostrar errores generales si existen
    state.generalError?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(5000) // Mostrar por 5 segundos
            viewModel.onAction(LoginScreenAction.OnDismissError)
        }
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}