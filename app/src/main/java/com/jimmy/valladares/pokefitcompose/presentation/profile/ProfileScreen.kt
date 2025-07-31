package com.jimmy.valladares.pokefitcompose.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jimmy.valladares.pokefitcompose.presentation.home.BottomNavItem
import com.jimmy.valladares.pokefitcompose.presentation.navigation.BottomNavigationBar

@Composable
fun ProfileScreen(
    onNavigateToTab: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F23))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "👤 Perfil",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Próximamente...",
                fontSize = 18.sp,
                color = Color(0xFF9CA3AF)
            )
        }
        
        BottomNavigationBar(
            selectedTab = BottomNavItem.PROFILE,
            onTabSelected = { tab -> onNavigateToTab(tab.route) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
