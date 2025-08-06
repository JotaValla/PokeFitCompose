package com.jimmy.valladares.pokefitcompose.data.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthService @Inject constructor() {
    
    private val auth: FirebaseAuth = Firebase.auth
    
    companion object {
        private const val TAG = "FirebaseAuthService"
    }
    
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    val currentUserId: String?
        get() = auth.currentUser?.uid
        
    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null
    
    // Flow para observar cambios en el estado de autenticación
    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }
    
    // Iniciar sesión con email y contraseña
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        return try {
            Log.d(TAG, "Attempting to sign in with email: $email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            
            if (result.user != null) {
                Log.d(TAG, "Sign in successful for user: ${result.user?.email}")
                AuthResult.Success(result.user!!)
            } else {
                Log.w(TAG, "Sign in failed: User is null")
                AuthResult.Error("Error desconocido al iniciar sesión")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Sign in failed", e)
            AuthResult.Error(e.message ?: "Error desconocido al iniciar sesión")
        }
    }
    
    // Crear cuenta con email y contraseña
    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult {
        return try {
            Log.d(TAG, "Attempting to create user with email: $email")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            
            if (result.user != null) {
                Log.d(TAG, "User creation successful for: ${result.user?.email}")
                AuthResult.Success(result.user!!)
            } else {
                Log.w(TAG, "User creation failed: User is null")
                AuthResult.Error("Error desconocido al crear cuenta")
            }
        } catch (e: Exception) {
            Log.w(TAG, "User creation failed", e)
            AuthResult.Error(e.message ?: "Error desconocido al crear cuenta")
        }
    }
    
    // Cerrar sesión
    suspend fun signOut(): AuthResult {
        return try {
            Log.d(TAG, "Signing out user")
            auth.signOut()
            AuthResult.Success(null)
        } catch (e: Exception) {
            Log.w(TAG, "Sign out failed", e)
            AuthResult.Error(e.message ?: "Error al cerrar sesión")
        }
    }
    
    // Enviar email para restablecer contraseña
    suspend fun sendPasswordResetEmail(email: String): AuthResult {
        return try {
            Log.d(TAG, "Sending password reset email to: $email")
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(null)
        } catch (e: Exception) {
            Log.w(TAG, "Password reset email failed", e)
            AuthResult.Error(e.message ?: "Error al enviar email de recuperación")
        }
    }
    
    // Actualizar perfil del usuario
    suspend fun updateProfile(displayName: String? = null): AuthResult {
        return try {
            val user = currentUser ?: return AuthResult.Error("Usuario no autenticado")
            
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .apply {
                    displayName?.let { setDisplayName(it) }
                }
                .build()
                
            user.updateProfile(profileUpdates).await()
            Log.d(TAG, "Profile updated successfully")
            AuthResult.Success(user)
        } catch (e: Exception) {
            Log.w(TAG, "Profile update failed", e)
            AuthResult.Error(e.message ?: "Error al actualizar perfil")
        }
    }
}

sealed class AuthResult {
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
