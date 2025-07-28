package com.jimmy.valladares.pokefitcompose.presentation.login

sealed interface LoginScreenEvent {
    data object NavigateToHome : LoginScreenEvent
    data object NavigateToRegister : LoginScreenEvent
    data object NavigateToForgotPassword : LoginScreenEvent
}
