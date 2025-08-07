package com.jimmy.valladares.pokefitcompose.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jimmy.valladares.pokefitcompose.R
import com.jimmy.valladares.pokefitcompose.presentation.home.BottomNavItem
import com.jimmy.valladares.pokefitcompose.presentation.navigation.BottomNavigationBar

@Composable
fun ProfileScreen(
    onNavigateToTab: (String) -> Unit = {},
    onNavigateToWelcome: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileEvent.NavigateToWelcome -> onNavigateToWelcome()
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = stringResource(R.string.profile_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Círculo con User.gif (basado en el diseño del Pokémon)
            UserProfileCircle(
                userProfile = state.userProfile
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Información del usuario
            UserInfoCard(
                userProfile = state.userProfile,
                userEmail = state.userEmail
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Logout Button
            Button(
                onClick = { viewModel.onAction(ProfileAction.OnSignOutClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC2626)
                ),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = stringResource(R.string.sign_out),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.sign_out),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
            
            // Error Message
            state.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = Color(0xFFEF4444),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        BottomNavigationBar(
            selectedTab = BottomNavItem.PROFILE,
            onTabSelected = { tab -> onNavigateToTab(tab.route) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun UserProfileCircle(
    userProfile: com.jimmy.valladares.pokefitcompose.data.model.UserProfile?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(140.dp) // Mismo tamaño que el círculo del Pokémon
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
                .data("file:///android_asset/user.gif")
                .placeholder(R.drawable.pokeball)
                .error(R.drawable.pokeball)
                .build(),
            contentDescription = "Perfil de usuario",
            modifier = Modifier.size(100.dp) // Mismo tamaño que la imagen del Pokémon
        )
    }
}

@Composable
private fun UserInfoCard(
    userProfile: com.jimmy.valladares.pokefitcompose.data.model.UserProfile?,
    userEmail: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nombre
            Text(
                text = userProfile?.name ?: "Usuario",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Email
            Text(
                text = userEmail,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información en filas
            UserInfoRow(
                label = "Edad:",
                value = "${userProfile?.age ?: 0} años"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            UserInfoRow(
                label = "Peso:",
                value = "${userProfile?.weight ?: 0} kg"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            UserInfoRow(
                label = "Pokémon:",
                value = userProfile?.selectedPokemon?.replaceFirstChar { it.uppercase() } ?: "Ninguno"
            )
        }
    }
}

@Composable
private fun UserInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color(0xFF8B5CF6),
            fontWeight = FontWeight.SemiBold
        )
    }
}
