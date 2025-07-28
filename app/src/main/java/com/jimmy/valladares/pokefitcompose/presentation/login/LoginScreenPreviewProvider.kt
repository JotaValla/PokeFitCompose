package com.jimmy.valladares.pokefitcompose.presentation.login

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class LoginScreenStatePreviewProvider : PreviewParameterProvider<LoginScreenState> {
    override val values = sequenceOf(
        LoginScreenState(
            isLoading = false,
            emailError = null,
            passwordError = null,
            generalError = null
        ),
        LoginScreenState(
            email = "usuario@ejemplo.com",
            password = "password123",
            isLoading = false,
            emailError = null,
            passwordError = null,
            generalError = null
        ),
        LoginScreenState(
            isLoading = true,
            emailError = null,
            passwordError = null,
            generalError = null
        ),
        LoginScreenState(
            email = "email_invalido",
            password = "123",
            isLoading = false,
            emailError = "Formato de email inválido",
            passwordError = "La contraseña debe tener al menos 6 caracteres",
            generalError = null
        ),
        LoginScreenState(
            isLoading = false,
            emailError = null,
            passwordError = null,
            generalError = "Error de conexión. Inténtalo de nuevo."
        )
    )
}
