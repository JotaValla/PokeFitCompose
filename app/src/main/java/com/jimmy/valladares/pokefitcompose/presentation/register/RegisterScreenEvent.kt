package com.jimmy.valladares.pokefitcompose.presentation.register

sealed interface RegisterScreenEvent {
    data object NavigateToHome : RegisterScreenEvent
    data object NavigateToLogin : RegisterScreenEvent
}
