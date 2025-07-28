package com.jimmy.valladares.pokefitcompose.presentation.welcome

sealed interface WelcomeScreenAction {
    data object OnGetStarted : WelcomeScreenAction
}
