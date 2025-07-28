package com.jimmy.valladares.pokefitcompose.presentation.welcome

sealed interface WelcomeScreenEvent {
    data object NavigateToLogin : WelcomeScreenEvent
}
