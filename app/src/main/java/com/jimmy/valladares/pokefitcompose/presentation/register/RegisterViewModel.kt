package com.jimmy.valladares.pokefitcompose.presentation.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.valladares.pokefitcompose.data.auth.AuthResult
import com.jimmy.valladares.pokefitcompose.data.auth.FirebaseAuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authService: FirebaseAuthService
) : ViewModel() {
    
    private val _state = MutableStateFlow(RegisterScreenState())
    val state: StateFlow<RegisterScreenState> = _state.asStateFlow()
    
    private val _events = Channel<RegisterScreenEvent>()
    val events = _events.receiveAsFlow()
    
    companion object {
        private const val TAG = "RegisterViewModel"
    }
    
    fun onAction(action: RegisterScreenAction) {
        when (action) {
            is RegisterScreenAction.OnNameChange -> {
                _state.value = _state.value.copy(
                    fullName = action.name,
                    fullNameError = null
                )
            }
            is RegisterScreenAction.OnEmailChange -> {
                _state.value = _state.value.copy(
                    email = action.email,
                    emailError = null
                )
            }
            is RegisterScreenAction.OnPasswordChange -> {
                _state.value = _state.value.copy(
                    password = action.password,
                    passwordError = null
                )
            }
            is RegisterScreenAction.OnConfirmPasswordChange -> {
                _state.value = _state.value.copy(
                    confirmPassword = action.confirmPassword,
                    confirmPasswordError = null
                )
            }
            is RegisterScreenAction.OnAcceptTermsChange -> {
                _state.value = _state.value.copy(
                    acceptTerms = action.accept
                )
            }
            is RegisterScreenAction.OnCreateAccountClick -> {
                handleCreateAccount()
            }
            is RegisterScreenAction.OnTogglePasswordVisibility -> {
                _state.value = _state.value.copy(
                    isPasswordVisible = !_state.value.isPasswordVisible
                )
            }
            is RegisterScreenAction.OnToggleConfirmPasswordVisibility -> {
                _state.value = _state.value.copy(
                    isConfirmPasswordVisible = !_state.value.isConfirmPasswordVisible
                )
            }
            is RegisterScreenAction.OnLoginClick -> {
                handleLoginNavigation()
            }
            is RegisterScreenAction.OnDismissError -> {
                _state.value = _state.value.copy(
                    fullNameError = null,
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null,
                    generalError = null
                )
            }
        }
    }
    
    private fun handleCreateAccount() {
        if (validateInputs()) {
            _state.value = _state.value.copy(isLoading = true)
            
            viewModelScope.launch {
                try {
                    val email = _state.value.email.trim()
                    val password = _state.value.password
                    val fullName = _state.value.fullName.trim()
                    
                    Log.d(TAG, "Attempting to create account with Firebase for email: $email")
                    
                    when (val result = authService.createUserWithEmailAndPassword(email, password)) {
                        is AuthResult.Success -> {
                            Log.d(TAG, "Firebase account creation successful")
                            
                            // Actualizar el perfil con el nombre completo
                            val updateResult = authService.updateProfile(displayName = fullName)
                            when (updateResult) {
                                is AuthResult.Success -> {
                                    Log.d(TAG, "Profile update successful")
                                }
                                is AuthResult.Error -> {
                                    Log.w(TAG, "Profile update failed: ${updateResult.message}")
                                    // No es crítico, continuar con el flujo
                                }
                            }
                            
                            _state.value = _state.value.copy(
                                isLoading = false,
                                generalError = null
                            )
                            _events.send(RegisterScreenEvent.NavigateToHome)
                        }
                        is AuthResult.Error -> {
                            Log.w(TAG, "Firebase account creation failed: ${result.message}")
                            _state.value = _state.value.copy(
                                isLoading = false,
                                generalError = getFirebaseErrorMessage(result.message)
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Unexpected error during account creation", e)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        generalError = "Error inesperado. Por favor intenta de nuevo."
                    )
                }
            }
        }
    }
    
    private fun getFirebaseErrorMessage(error: String): String {
        return when {
            error.contains("invalid-email") -> "El formato del email es inválido"
            error.contains("email-already-in-use") -> "Ya existe una cuenta con este email"
            error.contains("weak-password") -> "La contraseña es muy débil. Debe tener al menos 6 caracteres"
            error.contains("network-request-failed") -> "Error de conexión. Verifica tu internet"
            error.contains("too-many-requests") -> "Demasiados intentos. Intenta más tarde"
            error.contains("invalid-credential") -> "Credenciales inválidas. Verifica tu email y contraseña"
            error.contains("user-disabled") -> "Esta cuenta ha sido deshabilitada"
            error.contains("operation-not-allowed") -> "Operación no permitida. Contacta al soporte"
            else -> "Error de autenticación: $error"
        }
    }
    
    private fun handleLoginNavigation() {
        viewModelScope.launch {
            _events.send(RegisterScreenEvent.NavigateToLogin)
        }
    }
    
    private fun validateInputs(): Boolean {
        val fullName = _state.value.fullName.trim()
        val email = _state.value.email.trim()
        val password = _state.value.password
        val confirmPassword = _state.value.confirmPassword
        
        var hasErrors = false
        
        // Validar nombre completo
        if (fullName.isEmpty()) {
            _state.value = _state.value.copy(fullNameError = "El nombre completo es requerido")
            hasErrors = true
        } else if (fullName.length < 2) {
            _state.value = _state.value.copy(fullNameError = "El nombre debe tener al menos 2 caracteres")
            hasErrors = true
        } else {
            _state.value = _state.value.copy(fullNameError = null)
        }
        
        // Validar email
        if (email.isEmpty()) {
            _state.value = _state.value.copy(emailError = "El email es requerido")
            hasErrors = true
        } else if (!isValidEmail(email)) {
            _state.value = _state.value.copy(emailError = "Formato de email inválido")
            hasErrors = true
        } else {
            _state.value = _state.value.copy(emailError = null)
        }
        
        // Validar contraseña
        if (password.isEmpty()) {
            _state.value = _state.value.copy(passwordError = "La contraseña es requerida")
            hasErrors = true
        } else if (password.length < 6) {
            _state.value = _state.value.copy(passwordError = "La contraseña debe tener al menos 6 caracteres")
            hasErrors = true
        } else {
            _state.value = _state.value.copy(passwordError = null)
        }
        
        // Validar confirmación de contraseña
        if (confirmPassword.isEmpty()) {
            _state.value = _state.value.copy(confirmPasswordError = "Confirma tu contraseña")
            hasErrors = true
        } else if (password != confirmPassword) {
            _state.value = _state.value.copy(confirmPasswordError = "Las contraseñas no coinciden")
            hasErrors = true
        } else {
            _state.value = _state.value.copy(confirmPasswordError = null)
        }
        
        // Validar términos y condiciones
        if (!_state.value.acceptTerms) {
            _state.value = _state.value.copy(generalError = "Debes aceptar los términos y condiciones")
            hasErrors = true
        } else {
            _state.value = _state.value.copy(generalError = null)
        }
        
        return !hasErrors
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
