package com.jimmy.valladares.pokefitcompose.presentation.login

sealed interface LoginScreenAction {
    data class OnEmailChange(val email: String) : LoginScreenAction
    data class OnPasswordChange(val password: String) : LoginScreenAction
    data object OnSignInClick : LoginScreenAction
    data object OnTogglePasswordVisibility : LoginScreenAction
    data object OnForgotPasswordClick : LoginScreenAction
    data object OnRegisterClick : LoginScreenAction
    data object OnDismissError : LoginScreenAction
}
