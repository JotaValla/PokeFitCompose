package com.jimmy.valladares.pokefitcompose.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jimmy.valladares.pokefitcompose.data.auth.AuthResult
import com.jimmy.valladares.pokefitcompose.data.model.UserProfile
import com.jimmy.valladares.pokefitcompose.data.model.WorkoutSession
import com.jimmy.valladares.pokefitcompose.data.model.WorkoutSummary
import com.jimmy.valladares.pokefitcompose.data.result.FirestoreResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor() {
    
    private val firestore: FirebaseFirestore = Firebase.firestore
    
    companion object {
        private const val TAG = "FirestoreService"
        private const val USERS_COLLECTION = "users"
        private const val WORKOUTS_COLLECTION = "workouts"
        private const val WORKOUT_SUMMARIES_COLLECTION = "workout_summaries"
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
    
    // ==================== WORKOUT FUNCTIONS ====================
    
    suspend fun saveWorkoutSession(workoutSession: WorkoutSession): FirestoreResult<String> {
        return try {
            Log.d(TAG, "Saving workout session for user: ${workoutSession.userId}")
            
            // Generar ID único para el entrenamiento
            val workoutId = if (workoutSession.id.isBlank()) {
                firestore.collection(WORKOUTS_COLLECTION).document().id
            } else {
                workoutSession.id
            }
            
            val workoutToSave = workoutSession.copy(id = workoutId)
            
            // Guardar el entrenamiento
            firestore.collection(WORKOUTS_COLLECTION)
                .document(workoutId)
                .set(workoutToSave)
                .await()
            
            // Actualizar las estadísticas del usuario
            updateWorkoutSummary(workoutSession.userId, workoutToSave)
            
            // Actualizar el contador de entrenamientos en el perfil del usuario
            updateUserWorkoutCount(workoutSession.userId)
            
            Log.d(TAG, "Workout session saved successfully with ID: $workoutId")
            FirestoreResult.Success(workoutId)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving workout session", e)
            FirestoreResult.Error(e.message ?: "Error al guardar el entrenamiento")
        }
    }
    
    suspend fun getUserWorkouts(userId: String, limit: Int = 20): List<WorkoutSession> {
        return try {
            Log.d(TAG, "Getting workouts for user: $userId")
            
            // Solución temporal: Obtener todos los entrenamientos del usuario y ordenar en el cliente
            val documents = firestore.collection(WORKOUTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val workouts = documents.documents.mapNotNull { document ->
                document.toObject(WorkoutSession::class.java)
            }.sortedByDescending { it.completedAt }
                .take(limit)
            
            Log.d(TAG, "Retrieved ${workouts.size} workouts for user")
            workouts
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user workouts", e)
            emptyList()
        }
    }
    
    suspend fun getWorkoutSummary(userId: String): WorkoutSummary? {
        return try {
            Log.d(TAG, "Getting workout summary for user: $userId")
            
            val document = firestore.collection(WORKOUT_SUMMARIES_COLLECTION)
                .document(userId)
                .get()
                .await()
            
            if (document.exists()) {
                val summary = document.toObject(WorkoutSummary::class.java)
                Log.d(TAG, "Workout summary retrieved successfully")
                summary
            } else {
                Log.d(TAG, "Workout summary not found, creating new one")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting workout summary", e)
            null
        }
    }
    
    private suspend fun updateWorkoutSummary(userId: String, workout: WorkoutSession) {
        try {
            Log.d(TAG, "Updating workout summary for user: $userId")
            
            val summaryRef = firestore.collection(WORKOUT_SUMMARIES_COLLECTION)
                .document(userId)
            
            val currentSummary = summaryRef.get().await()
                .toObject(WorkoutSummary::class.java) ?: WorkoutSummary(userId = userId)
            
            // Calcular nuevas estadísticas
            val totalSets = workout.exercises.sumOf { it.completedSets }
            val totalExercises = workout.exercises.size
            
            // Actualizar ejercicios favoritos
            val updatedFavoriteExercises = currentSummary.favoriteExercises.toMutableMap()
            workout.exercises.forEach { exercise ->
                updatedFavoriteExercises[exercise.name] = 
                    (updatedFavoriteExercises[exercise.name] ?: 0) + 1
            }
            
            // Calcular racha (streak)
            val currentStreak = calculateStreak(userId, workout.completedAt)
            
            val updatedSummary = currentSummary.copy(
                totalWorkouts = currentSummary.totalWorkouts + 1,
                totalExercises = currentSummary.totalExercises + totalExercises,
                totalSets = currentSummary.totalSets + totalSets,
                totalTimeSeconds = currentSummary.totalTimeSeconds + workout.totalDurationSeconds,
                lastWorkoutDate = workout.completedAt,
                currentStreak = currentStreak,
                longestStreak = maxOf(currentSummary.longestStreak, currentStreak),
                favoriteExercises = updatedFavoriteExercises,
                updatedAt = System.currentTimeMillis()
            )
            
            summaryRef.set(updatedSummary).await()
            Log.d(TAG, "Workout summary updated successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating workout summary", e)
        }
    }
    
    private suspend fun updateUserWorkoutCount(userId: String) {
        try {
            val userRef = firestore.collection(USERS_COLLECTION).document(userId)
            val userDoc = userRef.get().await()
            
            if (userDoc.exists()) {
                val currentCount = userDoc.getLong("totalWorkouts") ?: 0
                userRef.update(
                    mapOf(
                        "totalWorkouts" to (currentCount + 1),
                        "updatedAt" to System.currentTimeMillis()
                    )
                ).await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user workout count", e)
        }
    }
    
    private suspend fun calculateStreak(userId: String, currentWorkoutDate: Long): Int {
        return try {
            // Obtener los últimos entrenamientos para calcular la racha
            val workouts = firestore.collection(WORKOUTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("completedAt", Query.Direction.DESCENDING)
                .limit(30) // Últimos 30 entrenamientos
                .get()
                .await()
            
            val workoutDates = workouts.documents.mapNotNull { doc ->
                doc.getLong("completedAt")
            }.map { timestamp ->
                // Convertir a días desde epoch para comparar fechas
                timestamp / (24 * 60 * 60 * 1000)
            }.distinct().sorted().reversed()
            
            if (workoutDates.isEmpty()) return 1
            
            var streak = 1
            var previousDay = workoutDates[0]
            
            for (i in 1 until workoutDates.size) {
                val currentDay = workoutDates[i]
                if (previousDay - currentDay == 1L) {
                    streak++
                    previousDay = currentDay
                } else {
                    break
                }
            }
            
            streak
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating streak", e)
            1
        }
    }
}
