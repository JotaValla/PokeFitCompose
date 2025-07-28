package com.jimmy.valladares.pokefitcompose.presentation.register

sealed interface RegisterScreenAction {
    data class OnEmailChange(val email: String) : RegisterScreenAction
    data class OnPasswordChange(val password: String) : RegisterScreenAction
    data class OnConfirmPasswordChange(val confirmPassword: String) : RegisterScreenAction
    data class OnNameChange(val name: String) : RegisterScreenAction
    data object OnCreateAccountClick : RegisterScreenAction
    data object OnTogglePasswordVisibility : RegisterScreenAction
    data object OnToggleConfirmPasswordVisibility : RegisterScreenAction
    data object OnLoginClick : RegisterScreenAction
    data object OnDismissError : RegisterScreenAction
    data class OnAcceptTermsChange(val accept: Boolean) : RegisterScreenAction
}
