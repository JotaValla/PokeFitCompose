package com.jimmy.valladares.pokefitcompose.presentation.register

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class RegisterScreenStatePreviewProvider : PreviewParameterProvider<RegisterScreenState> {
    override val values = sequenceOf(
        RegisterScreenState(
            isLoading = false,
            fullNameError = null,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            generalError = null
        ),
        RegisterScreenState(
            fullName = "Juan Pérez",
            email = "juan@ejemplo.com",
            password = "password123",
            confirmPassword = "password123",
            acceptTerms = true,
            isLoading = false,
            fullNameError = null,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            generalError = null
        ),
        RegisterScreenState(
            isLoading = true,
            fullNameError = null,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            generalError = null
        ),
        RegisterScreenState(
            fullName = "J",
            email = "email_invalido",
            password = "123",
            confirmPassword = "456",
            isLoading = false,
            fullNameError = "El nombre debe tener al menos 2 caracteres",
            emailError = "Formato de email inválido",
            passwordError = "La contraseña debe tener al menos 6 caracteres",
            confirmPasswordError = "Las contraseñas no coinciden",
            generalError = null
        ),
        RegisterScreenState(
            isLoading = false,
            fullNameError = null,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            generalError = "Error al crear la cuenta. Inténtalo de nuevo."
        )
    )
}
