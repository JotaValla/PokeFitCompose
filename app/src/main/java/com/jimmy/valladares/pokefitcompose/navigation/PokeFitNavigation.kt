package com.jimmy.valladares.pokefitcompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jimmy.valladares.pokefitcompose.presentation.home.HomeScreen
import com.jimmy.valladares.pokefitcompose.presentation.initial_survey.InitialSurveyScreenRoot
import com.jimmy.valladares.pokefitcompose.presentation.login.LoginScreen
import com.jimmy.valladares.pokefitcompose.presentation.pokemon_selection.PokemonSelectionScreenRoot
import com.jimmy.valladares.pokefitcompose.presentation.register.RegisterScreen
import com.jimmy.valladares.pokefitcompose.presentation.steps_survey.StepsSurveyScreenRoot
import com.jimmy.valladares.pokefitcompose.presentation.training_survey.TrainingSurveyScreenRoot
import com.jimmy.valladares.pokefitcompose.presentation.welcome.WelcomeScreenRoot

@Composable
fun PokeFitNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = WelcomeDestination
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
        
        composable<HomeDestination> {
            HomeScreen()
        }
    }
}
