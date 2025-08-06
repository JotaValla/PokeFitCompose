package com.jimmy.valladares.pokefitcompose.presentation.login

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
class LoginViewModel @Inject constructor(
    private val authService: FirebaseAuthService
) : ViewModel() {
    
    private val _state = MutableStateFlow(LoginScreenState())
    val state: StateFlow<LoginScreenState> = _state.asStateFlow()
    
    private val _events = Channel<LoginScreenEvent>()
    val events = _events.receiveAsFlow()
    
    companion object {
        private const val TAG = "LoginViewModel"
    }
    
    init {
        // Verificar si el usuario ya está logueado
        if (authService.isUserLoggedIn) {
            Log.d(TAG, "User already logged in: ${authService.currentUser?.email}")
            viewModelScope.launch {
                _events.send(LoginScreenEvent.NavigateToHome)
            }
        }
    }
    
    fun onAction(action: LoginScreenAction) {
        when (action) {
            is LoginScreenAction.OnEmailChange -> {
                _state.value = _state.value.copy(
                    email = action.email,
                    emailError = null
                )
            }
            is LoginScreenAction.OnPasswordChange -> {
                _state.value = _state.value.copy(
                    password = action.password,
                    passwordError = null
                )
            }
            is LoginScreenAction.OnSignInClick -> {
                handleSignIn()
            }
            is LoginScreenAction.OnTogglePasswordVisibility -> {
                _state.value = _state.value.copy(
                    isPasswordVisible = !_state.value.isPasswordVisible
                )
            }
            is LoginScreenAction.OnForgotPasswordClick -> {
                handleForgotPassword()
            }
            is LoginScreenAction.OnRegisterClick -> {
                handleRegisterNavigation()
            }
            is LoginScreenAction.OnDismissError -> {
                _state.value = _state.value.copy(
                    emailError = null,
                    passwordError = null,
                    generalError = null
                )
            }
        }
    }
    
    private fun handleSignIn() {
        if (validateInputs()) {
            _state.value = _state.value.copy(isLoading = true)
            
            viewModelScope.launch {
                try {
                    val email = _state.value.email.trim()
                    val password = _state.value.password
                    
                    Log.d(TAG, "Attempting to sign in with Firebase for email: $email")
                    
                    when (val result = authService.signInWithEmailAndPassword(email, password)) {
                        is AuthResult.Success -> {
                            Log.d(TAG, "Firebase sign in successful")
                            _state.value = _state.value.copy(
                                isLoading = false,
                                generalError = null
                            )
                            _events.send(LoginScreenEvent.NavigateToHome)
                        }
                        is AuthResult.Error -> {
                            Log.w(TAG, "Firebase sign in failed: ${result.message}")
                            _state.value = _state.value.copy(
                                isLoading = false,
                                generalError = getFirebaseErrorMessage(result.message)
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Unexpected error during sign in", e)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        generalError = "Error inesperado. Por favor intenta de nuevo."
                    )
                }
            }
        }
    }
    
    private fun handleForgotPassword() {
        if (_state.value.email.trim().isEmpty()) {
            _state.value = _state.value.copy(
                emailError = "Ingresa tu email para recuperar la contraseña"
            )
            return
        }
        
        if (!isValidEmail(_state.value.email.trim())) {
            _state.value = _state.value.copy(
                emailError = "Formato de email inválido"
            )
            return
        }
        
        viewModelScope.launch {
            try {
                val email = _state.value.email.trim()
                Log.d(TAG, "Sending password reset email to: $email")
                
                when (val result = authService.sendPasswordResetEmail(email)) {
                    is AuthResult.Success -> {
                        Log.d(TAG, "Password reset email sent successfully")
                        _events.send(LoginScreenEvent.ShowMessage("Email de recuperación enviado a $email"))
                    }
                    is AuthResult.Error -> {
                        Log.w(TAG, "Failed to send password reset email: ${result.message}")
                        _state.value = _state.value.copy(
                            generalError = getFirebaseErrorMessage(result.message)
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error sending password reset email", e)
                _state.value = _state.value.copy(
                    generalError = "Error al enviar email de recuperación"
                )
            }
        }
    }
    
    private fun getFirebaseErrorMessage(error: String): String {
        return when {
            error.contains("invalid-email") -> "El formato del email es inválido"
            error.contains("user-disabled") -> "Esta cuenta ha sido deshabilitada"
            error.contains("user-not-found") -> "No existe una cuenta con este email"
            error.contains("wrong-password") -> "La contraseña es incorrecta"
            error.contains("too-many-requests") -> "Demasiados intentos fallidos. Intenta más tarde"
            error.contains("network-request-failed") -> "Error de conexión. Verifica tu internet"
            error.contains("invalid-credential") -> "Credenciales inválidas. Verifica tu email y contraseña"
            else -> error
        }
    }
    
    private fun handleRegisterNavigation() {
        viewModelScope.launch {
            _events.send(LoginScreenEvent.NavigateToRegister)
        }
    }
    
    private fun validateInputs(): Boolean {
        val email = _state.value.email.trim()
        val password = _state.value.password
        
        var hasErrors = false
        
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
        
        return !hasErrors
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
