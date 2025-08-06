package com.jimmy.valladares.pokefitcompose.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jimmy.valladares.pokefitcompose.data.auth.AuthResult
import com.jimmy.valladares.pokefitcompose.data.model.UserProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor() {
    
    private val firestore: FirebaseFirestore = Firebase.firestore
    
    companion object {
        private const val TAG = "FirestoreService"
        private const val USERS_COLLECTION = "users"
    }
    
    suspend fun saveUserProfile(userProfile: UserProfile): AuthResult {
        return try {
            Log.d(TAG, "Saving user profile for UID: ${userProfile.uid}")
            
            firestore.collection(USERS_COLLECTION)
                .document(userProfile.uid)
                .set(userProfile)
                .await()
                
            Log.d(TAG, "User profile saved successfully")
            AuthResult.Success(null)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user profile", e)
            AuthResult.Error(e.message ?: "Error al guardar el perfil de usuario")
        }
    }
    
    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            Log.d(TAG, "Getting user profile for UID: $uid")
            
            val document = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
                
            if (document.exists()) {
                val userProfile = document.toObject(UserProfile::class.java)
                Log.d(TAG, "User profile retrieved successfully")
                userProfile
            } else {
                Log.d(TAG, "User profile not found")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user profile", e)
            null
        }
    }
    
    suspend fun updateUserProfile(uid: String, updates: Map<String, Any>): AuthResult {
        return try {
            Log.d(TAG, "Updating user profile for UID: $uid")
            
            val updatedData = updates + mapOf("updatedAt" to System.currentTimeMillis())
            
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .update(updatedData)
                .await()
                
            Log.d(TAG, "User profile updated successfully")
            AuthResult.Success(null)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile", e)
            AuthResult.Error(e.message ?: "Error al actualizar el perfil de usuario")
        }
    }
}
