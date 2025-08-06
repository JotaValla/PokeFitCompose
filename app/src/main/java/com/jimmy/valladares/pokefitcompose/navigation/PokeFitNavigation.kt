package com.jimmy.valladares.pokefitcompose.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jimmy.valladares.pokefitcompose.presentation.home.HomeScreen
import com.jimmy.valladares.pokefitcompose.presentation.initial_survey.InitialSurveyScreenRoot
import com.jimmy.valladares.pokefitcompose.presentation.login.LoginScreen
import com.jimmy.valladares.pokefitcompose.presentation.main.MainViewModel
import com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection.PokemonSelectionScreenRoot
import com.jimmy.valladares.pokefitcompose.presentation.profile.ProfileScreen
import com.jimmy.valladares.pokefitcompose.presentation.register.RegisterScreen
import com.jimmy.valladares.pokefitcompose.presentation.stats.StatsScreen
import com.jimmy.valladares.pokefitcompose.presentation.steps_survey.StepsSurveyScreenRoot
import com.jimmy.valladares.pokefitcompose.presentation.strength_training.StrengthTrainingScreen
import com.jimmy.valladares.pokefitcompose.presentation.training.TrainingScreen
import com.jimmy.valladares.pokefitcompose.presentation.training_survey.TrainingSurveyScreenRoot
import com.jimmy.valladares.pokefitcompose.presentation.welcome.WelcomeScreenRoot

@Composable
fun PokeFitNavigation(
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val mainState by mainViewModel.state.collectAsState()
    
    // Mostrar pantalla de carga mientras se verifica la autenticación
    if (mainState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    // Determinar el destino inicial basado en el estado de autenticación
    val startDestination = if (mainState.isAuthenticated) {
        HomeDestination
    } else {
        WelcomeDestination
    }
    
    LaunchedEffect(mainState.isAuthenticated) {
        if (mainState.isAuthenticated) {
            // Si el usuario está autenticado, navegar a Home
            navController.navigate(HomeDestination) {
                popUpTo(0) { inclusive = true }
            }
        } else {
            // Si no está autenticado, navegar a Welcome
            navController.navigate(WelcomeDestination) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<WelcomeDestination> {
            WelcomeScreenRoot(
                navigateToLogin = {
                    navController.navigate(LoginDestination) {
                        popUpTo(WelcomeDestination) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
        composable<LoginDestination> {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(HomeDestination) {
                        popUpTo(LoginDestination) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(RegisterDestination)
                },
                onNavigateToForgotPassword = {
                    // TODO: Implementar pantalla de recuperación de contraseña
                }
            )
        }
        
        composable<RegisterDestination> {
            RegisterScreen(
                onNavigateToHome = {
                    navController.navigate(InitialSurveyDestination) {
                        popUpTo(RegisterDestination) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<InitialSurveyDestination> {
            InitialSurveyScreenRoot(
                onNavigateToNextStep = {
                    // Este caso ya no se usa, pero lo mantenemos por compatibilidad
                    navController.navigate(HomeDestination) {
                        popUpTo(InitialSurveyDestination) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToTrainingSurvey = {
                    // Para "Mejorar mi entrenamiento" - va a la encuesta de entrenamiento
                    navController.navigate(TrainingSurveyDestination)
                },
                onNavigateToStepsSurvey = {
                    // Para "Salir de sedentarismo" - va a la encuesta de pasos
                    navController.navigate(StepsSurveyDestination)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<TrainingSurveyDestination> {
            TrainingSurveyScreenRoot(
                onNavigateToNextStep = {
                    navController.navigate(PokemonSelectionDestination) {
                        popUpTo(TrainingSurveyDestination) {
                            inclusive = true
                        }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<StepsSurveyDestination> {
            StepsSurveyScreenRoot(
                onNavigateToNextStep = {
                    navController.navigate(PokemonSelectionDestination) {
                        popUpTo(StepsSurveyDestination) {
                            inclusive = true
                        }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable<PokemonSelectionDestination> {
            PokemonSelectionScreenRoot(
                onNavigateToHome = {
                    navController.navigate(HomeDestination) {
                        popUpTo(PokemonSelectionDestination) {
                            inclusive = true
                        }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Main app navigation with bottom navigation
        composable<HomeDestination> {
            HomeScreen(
                onNavigateToTraining = {
                    navController.navigate(TrainingDestination)
                },
                onNavigateToTab = { route ->
                    when (route) {
                        "home" -> {
                            // Ya estamos en home, no hacer nada
                        }
                        "stats" -> navController.navigate(StatsDestination)
                        "training" -> navController.navigate(TrainingDestination)
                        "profile" -> navController.navigate(ProfileDestination)
                    }
                }
            )
        }
        
        composable<StatsDestination> {
            StatsScreen(
                onNavigateToTab = { route ->
                    when (route) {
                        "home" -> navController.navigate(HomeDestination)
                        "stats" -> {
                            // Ya estamos en stats, no hacer nada
                        }
                        "training" -> navController.navigate(TrainingDestination)
                        "profile" -> navController.navigate(ProfileDestination)
                    }
                }
            )
        }
        
        composable<TrainingDestination> {
            StrengthTrainingScreen(
                onNavigateToTab = { route ->
                    when (route) {
                        "home" -> navController.navigate(HomeDestination)
                        "stats" -> navController.navigate(StatsDestination)
                        "training" -> {
                            // Ya estamos en training, no hacer nada
                        }
                        "profile" -> navController.navigate(ProfileDestination)
                    }
                }
            )
        }
        
        composable<ProfileDestination> {
            ProfileScreen(
                onNavigateToTab = { route ->
                    when (route) {
                        "home" -> navController.navigate(HomeDestination)
                        "stats" -> navController.navigate(StatsDestination)
                        "training" -> navController.navigate(TrainingDestination)
                        "profile" -> {
                            // Ya estamos en profile, no hacer nada
                        }
                    }
                },
                onNavigateToWelcome = {
                    navController.navigate(WelcomeDestination) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
